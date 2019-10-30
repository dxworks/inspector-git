package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.CommitDTO
import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.model.Author
import org.dxworks.inspectorgit.model.AuthorID
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CommitTransformer(private val commitDTO: CommitDTO, private val project: Project) {
    companion object {
        private const val dateFormat = "EEE MMM d HH:mm:ss yyyy Z"
        private val LOG = LoggerFactory.getLogger(CommitTransformer::class.java)
    }

    fun transform(): Commit {
        LOG.info("Creating commit with id: ${commitDTO.id}")

        val author = getAuthor(commitDTO, project)
        LOG.info("Parsed author")
        val committer = getCommitter(commitDTO, project)
        LOG.info("Parsed committer")

        val commit = Commit(id = commitDTO.id,
                message = commitDTO.message,
                authorDate = parseDate(commitDTO.authorDate),
                committerDate = parseDate(commitDTO.committerDate),
                author = author,
                committer = committer,
                parents = getParentFromIds(commitDTO.parentIds, project),
                changes = ArrayList())

        LOG.info("Adding commit to authors")
        author.commits.add(commit)
        if (committer != author)
            committer.commits.add(commit)

        addChangesToCommit(commitDTO.changes, commit, project)

        LOG.info("Done creating commit with id: ${commitDTO.id}")

        return commit
    }

    private fun parseDate(timestamp: String) =
            LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(dateFormat))

    private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: Project) {
        LOG.info("Filtering changes")
        val admittedChanges = if (commit.isMergeCommit) filterChanges(changes, project) else changes
        LOG.info("Done filtering changes")
        commit.changes = admittedChanges.map { ChangeTransformer(it, commit, project).transform() }
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
}