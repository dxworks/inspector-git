package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.SimpleChangeFactory
import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitAccount
import org.dxworks.inspectorgit.model.git.GitAccountId
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

        val committer = if (commitDTO.committerName.isEmpty()) author else getCommitter(commitDTO, project)
        LOG.info("Parsed committer ${author.id}")


        val authorDate = parseDate(commitDTO.authorDate)
        val committerDate = if (commitDTO.committerDate.isEmpty()) authorDate else parseDate(commitDTO.committerDate)
        val commit = Commit(project = project,
                id = commitDTO.id,
                message = commitDTO.message,
                authorDate = authorDate,
                committerDate = committerDate,
                author = author,
                committer = committer,
                parents = parents,
                children = ArrayList(),
                changes = ArrayList())

        commit.parents.forEach { it.addChild(commit) }
        LOG.info("Adding commit to repository and to authors")

        project.commitRegistry.add(commit)
        author.commits += commit
        if (committer != author)
            committer.commits += commit

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
            val changesByFile = changes.groupBy {
                if (it.type == ChangeType.DELETE) it.oldFileName
                else it.newFileName
            }
            commit.changes = changesByFile.mapNotNull { MergeChangesTransformer(it.value, commit, project, changeFactory).transform() }.flatten()
        } else {
            commit.changes = changes.mapNotNull { ChangeTransformer(it, commit, project, changeFactory).transform() }
        }
        commit.changes.forEach { it.file.changes.add(it) }
        LOG.info("Transforming changes")
    }

    private fun getAuthor(commitDTO: CommitDTO, project: Project): GitAccount =
            getAuthor(project, GitAccountId(commitDTO.authorEmail, commitDTO.authorName))

    private fun getCommitter(commitDTO: CommitDTO, project: Project): GitAccount =
            getAuthor(project, GitAccountId(commitDTO.committerEmail, commitDTO.committerName))

    private fun getAuthor(project: Project, gitAccountId: GitAccountId): GitAccount {
        var author = project.accountRegistry.getById(gitAccountId.toString())
        if (author == null) {
            author = GitAccount(gitAccountId, project)
            project.accountRegistry.add(author)
        }
        return author as GitAccount
    }

    private fun getParentsFromIds(parentIds: List<String>, project: Project): List<Commit> =
            parentIds.mapNotNull { project.commitRegistry.getById(it) }
}