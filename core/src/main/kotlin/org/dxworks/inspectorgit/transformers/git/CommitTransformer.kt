package org.dxworks.inspectorgit.transformers.git

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitAccount
import org.dxworks.inspectorgit.model.git.GitAccountId
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.utils.commitDateTimeFormatter
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

class CommitTransformer {
    companion object {
        private val LOG = LoggerFactory.getLogger(CommitTransformer::class.java)

        fun addToProject(commitDTO: CommitDTO, project: GitProject, changeFactory: ChangeFactory = SimpleChangeFactory()) {
            LOG.debug("Creating commit with id: ${commitDTO.id}")
            val parents = getParentsFromIds(commitDTO.parentIds, project)
            if (parents.size > 1)
                LOG.debug("Is merge commit")

            val author = getAuthor(commitDTO, project)
            LOG.debug("Parsed author ${author.id}")

            val committer = if (commitDTO.committerName.isEmpty()) author else getCommitter(commitDTO, project)
            LOG.debug("Parsed committer ${author.id}")


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
            LOG.debug("Adding commit to repository and to authors")

            project.commitRegistry.add(commit)
            author.commits += commit
            if (committer != author)
                committer.commits += commit

            addChangesToCommit(commitDTO.changes, commit, project, changeFactory)

            commit.repoSize = getParentCommitSize(commit) + computeCommitGrowth(commit)

            LOG.debug("Done creating commit with id: ${commitDTO.id}")
        }

        private fun computeCommitGrowth(commit: Commit): Int {
            return commit.changes
                    .filter { commit.parents.isEmpty() || it.parentCommit == commit.parents.first() }
                    .map { it.addedLines.size - it.deletedLines.size }
                    .sum()
        }

        private fun getParentCommitSize(commit: Commit): Long {
            return commit.parents.firstOrNull()?.repoSize ?: 0
        }

        private fun parseDate(timestamp: String): ZonedDateTime {
            LOG.debug("Parsing date: $timestamp")
            return ZonedDateTime.parse(timestamp, commitDateTimeFormatter)
        }

        private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: GitProject, changeFactory: ChangeFactory) {
            LOG.debug("Filtering changes")
            if (commit.isMergeCommit) {
                val changesByFile = changes.groupBy {
                    if (it.type == ChangeType.DELETE) it.oldFileName
                    else it.newFileName
                }
                commit.changes = changesByFile.mapNotNull { MergeChangesTransformer.transform(it.value, commit, project, changeFactory) }.flatten()
            } else {
                commit.changes = changes.mapNotNull { ChangeTransformer.transform(it, commit, project, changeFactory) }
            }
            commit.changes.forEach { it.file.changes.add(it) }
            LOG.debug("Transforming changes")
        }

        private fun getAuthor(commitDTO: CommitDTO, project: GitProject): GitAccount =
                getAuthor(GitAccountId(commitDTO.authorEmail, commitDTO.authorName), project)

        private fun getCommitter(commitDTO: CommitDTO, project: GitProject): GitAccount =
                getAuthor(GitAccountId(commitDTO.committerEmail, commitDTO.committerName), project)

        private fun getAuthor(gitAccountId: GitAccountId, project: GitProject): GitAccount {
            var author = project.accountRegistry.getById(gitAccountId.toString())
            if (author == null) {
                author = GitAccount(gitAccountId, project)
                project.accountRegistry.add(author)
            }
            return author as GitAccount
        }

        private fun getParentsFromIds(parentIds: List<String>, project: GitProject): List<Commit> =
                parentIds.mapNotNull { project.commitRegistry.getById(it) }
    }
}
