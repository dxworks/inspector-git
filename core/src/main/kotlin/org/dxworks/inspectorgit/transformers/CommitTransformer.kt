package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.SimpleChangeFactory
import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.CommitDTO
import org.dxworks.inspectorgit.model.Author
import org.dxworks.inspectorgit.model.AuthorId
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.utils.commitDateTimeFormatter
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

class CommitTransformer(private val commitDTO: CommitDTO, private val project: Project, private val changeFactory: ChangeFactory = SimpleChangeFactory()) {
    companion object {
        private val LOG = LoggerFactory.getLogger(CommitTransformer::class.java)
    }

    fun addToProject() {
        LOG.info("Creating commit with id: ${commitDTO.id}")
        val parents = getParentsFromIds(commitDTO.parentIds, project)
        if (parents.size > 1)
            LOG.info("Is merge commit")

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
                parents = parents,
                children = ArrayList(),
                changes = ArrayList())

        commit.parents.forEach { it.addChild(commit) }
        LOG.info("Adding commit to repository and to authors")

        project.commitRegistry.add(commit)
        author.commits.add(commit)
        if (committer != author)
            committer.commits.add(commit)

        addChangesToCommit(commitDTO.changes, commit, project)

        LOG.info("Done creating commit with id: ${commitDTO.id}")
    }

    private fun parseDate(timestamp: String): ZonedDateTime {
        LOG.debug("Parsing date: $timestamp")
        return ZonedDateTime.parse(timestamp, commitDateTimeFormatter)
    }

    private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: Project) {
        LOG.info("Filtering changes")
        if (commit.isMergeCommit) {
//            val fixedChanges = RenameChangesDetector(changes, project).detectAndReplace()
            val changesByFile = changes.groupBy { it.newFileName }
            commit.changes = changesByFile.mapNotNull { MergeChangesTransformer(it.value, commit, project, changeFactory).transform() }.flatten()
        } else {
            commit.changes = changes.mapNotNull { ChangeTransformer(it, commit, project, changeFactory).transform() }
        }
        commit.changes.forEach { it.file.changes.add(it) }
        LOG.info("Transforming changes")
    }

    private fun getAuthor(commitDTO: CommitDTO, project: Project): Author =
            getAuthor(project, AuthorId(commitDTO.authorEmail, commitDTO.authorName))

    private fun getCommitter(commitDTO: CommitDTO, project: Project): Author =
            getAuthor(project, AuthorId(commitDTO.committerEmail, commitDTO.committerName))

    private fun getAuthor(project: Project, authorId: AuthorId): Author {
        var author = project.authorRegistry.getById(authorId)
        if (author == null) {
            author = Author(authorId)
            project.authorRegistry.add(author)
        }
        return author
    }

    private fun getParentsFromIds(parentIds: List<String>, project: Project): List<Commit> =
            parentIds.mapNotNull { project.commitRegistry.getById(it) }
}