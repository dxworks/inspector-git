package org.dxworks.inspectorgit.transformers

import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.CommitDTO
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.model.Author
import org.dxworks.inspectorgit.model.AuthorID
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Slf4j
class ProjectTransformer(private val projectDTO: ProjectDTO, private val projectId: String) {
    companion object {
        private const val dateFormat = "EEE MMM d HH:mm:ss yyyy Z"

        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)
    }

    fun transform(): Project {
        val project = Project(projectId)

        projectDTO.commits.forEach {
            LOG.info("Creating commit with id: ${it.id}")

            val author = getAuthor(it, project)
            LOG.info("Parsed author")
            val committer = getCommitter(it, project)
            LOG.info("Parsed committer")

            val commit = Commit(id = it.id,
                    message = it.message,
                    authorDate = parseDate(it.authorDate),
                    committerDate = parseDate(it.committerDate),
                    author = author,
                    committer = committer,
                    parents = getParentFromIds(it.parentIds, project),
                    changes = ArrayList())

            LOG.info("Adding commit to registry and authors")
            project.commitRegistry.add(commit)
            author.commits.add(commit)
            if (committer != author)
                committer.commits.add(commit)

            addChangesToCommit(it.changes, commit, project)

            LOG.info("Done creating commit with id: ${it.id}")
        }

        return project
    }

    private fun parseDate(timestamp: String) =
            LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(dateFormat))

    private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: Project) {
        LOG.info("Filtering changes")
        val admittedChanges = if (commit.isMergeCommit) filterChanges(changes, project) else changes
        LOG.info("Done filtering changes")
        commit.changes = admittedChanges.map { ChangeTransformer(it, commit, project).transform() }
    }

    private fun filterChanges(changes: List<ChangeDTO>, project: Project): List<ChangeDTO> {
        LOG.info("Getting potentially renamed files")
        val potentiallyRenamedFiles = changes.filter { it.type == ChangeType.ADD }
                .mapNotNull { project.fileRegistry.getByID(it.newFileName) }
        LOG.info("Found: ${potentiallyRenamedFiles.size}")
        LOG.info("Getting rename changes")
        val renameChanges = potentiallyRenamedFiles.mapNotNull { it.changes.find { change -> change.isRenameChange } }
        val newFileNames = renameChanges.map { it.newFileName }
        val oldFileNames = renameChanges.map { it.oldFileName }
        LOG.info("Found: ${renameChanges.size}")
        LOG.info("filtering legit changes")
        val legitChanges = changes.filter { !((it.type == ChangeType.ADD && newFileNames.contains(it.newFileName)) || (it.type == ChangeType.DELETE && oldFileNames.contains(it.oldFileName))) }
        val fakeAddChanges = changes.filter { (it.type == ChangeType.ADD && newFileNames.contains(it.newFileName)) }
        LOG.info("Found: ${legitChanges.size}")
        LOG.info("Substituting ${fakeAddChanges.size} add-delete pairs with rename changes")
        return legitChanges + fakeAddChanges.map {
            val renameChange = renameChanges.find { rc -> rc.newFileName == it.newFileName }!!
            ChangeDTO(oldFileName = renameChange.oldFileName,
                    newFileName = renameChange.newFileName,
                    type = ChangeType.RENAME,
                    parentCommitId = it.parentCommitId,
                    isBinary = it.isBinary,
                    hunks = emptyList(),
                    annotatedLines = it.annotatedLines,
                    isBlame = it.isBlame
            )
        }
    }

    private fun getAuthor(commitDTO: CommitDTO, project: Project): Author {
        return getAuthor(project, AuthorID(commitDTO.authorEmail, commitDTO.authorName))
    }

    private fun getCommitter(commitDTO: CommitDTO, project: Project): Author {
        return getAuthor(project, AuthorID(commitDTO.committerEmail, commitDTO.committerName))
    }

    private fun getAuthor(project: Project, authorID: AuthorID): Author {
        var author = project.authorRegistry.getByID(authorID)
        if (author == null) {
            author = Author(authorID)
            project.authorRegistry.add(author)
        }
        return author
    }

    private fun getParentFromIds(parentIds: List<String>, project: Project): List<Commit> {
        return parentIds.mapNotNull { project.commitRegistry.getByID(it) }
    }
}
