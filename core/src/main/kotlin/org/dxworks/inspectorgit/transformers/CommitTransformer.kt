package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.client.dto.ChangeDTO
import org.dxworks.inspectorgit.client.dto.CommitDTO
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

    fun addToProject() {
        LOG.info("Creating commit with id: ${commitDTO.id}")

        val author = getAuthor(commitDTO, project)
        LOG.info("Parsed author ${author.id}")

        val committer = getCommitter(commitDTO, project)
        LOG.info("Parsed committer ${author.id}")

        val commit = Commit(id = commitDTO.id,
                message = commitDTO.message,
                authorDate = parseDate(commitDTO.authorDate),
                committerDate = parseDate(commitDTO.committerDate),
                author = author,
                committer = committer,
                parents = getParentFromIds(commitDTO.parentIds, project),
                changes = ArrayList())

        LOG.info("Adding commit to repository and to authors")

        project.commitRegistry.add(commit)
        author.commits.add(commit)
        if (committer != author)
            committer.commits.add(commit)

        addChangesToCommit(commitDTO.changes, commit, project)

        LOG.info("Done creating commit with id: ${commitDTO.id}")
    }

    private fun parseDate(timestamp: String) =
            LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern(dateFormat))

    private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: Project) {
        LOG.info("Filtering changes")
        val admittedChanges = if (commit.isMergeCommit) RenameChangesDetector(changes, project).detectAndReplace() else changes
        LOG.info("Transforming changes")
        commit.changes = admittedChanges.map { ChangeTransformer(it, commit, project).transform() }
    }

    private fun getAuthor(commitDTO: CommitDTO, project: Project): Author =
            getAuthor(project, AuthorID(commitDTO.authorEmail, commitDTO.authorName))

    private fun getCommitter(commitDTO: CommitDTO, project: Project): Author =
            getAuthor(project, AuthorID(commitDTO.committerEmail, commitDTO.committerName))

    private fun getAuthor(project: Project, authorID: AuthorID): Author {
        var author = project.authorRegistry.getByID(authorID)
        if (author == null) {
            author = Author(authorID)
            project.authorRegistry.add(author)
        }
        return author
    }

    private fun getParentFromIds(parentIds: List<String>, project: Project): List<Commit> =
            parentIds.mapNotNull { project.commitRegistry.getByID(it) }
}