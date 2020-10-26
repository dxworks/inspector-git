package org.dxworks.inspectorgit.model.git

import org.dxworks.inspectorgit.model.issuetracker.Issue
import org.dxworks.inspectorgit.model.remote.CommitRemoteInfo
import org.dxworks.inspectorgit.model.remote.PullRequest
import java.time.Period
import java.time.ZonedDateTime

data class Commit(var project: GitProject,
                  var id: String,
                  var message: String,
                  val authorDate: ZonedDateTime,
                  val committerDate: ZonedDateTime,
                  val author: GitAccount,
                  val committer: GitAccount,
                  var parents: List<Commit>,
                  var children: List<Commit>,
                  var changes: List<Change>,
                  var issues: Set<Issue> = emptySet(),
                  var pullRequests: Set<PullRequest> = emptySet(),
                  var remoteInfo: CommitRemoteInfo? = null,
                  var branchId: Long = 0,
                  var repoSize: Long = 0) {
    fun olderThan(age: Period, other: Commit) = committerDate.isBefore(other.committerDate.minus(age))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Commit

        if (id != other.id) return false
        if (message != other.message) return false
        if (authorDate != other.authorDate) return false
        if (committerDate != other.committerDate) return false
        if (author != other.author) return false
        if (committer != other.committer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + message.hashCode()
        result = 31 * result + authorDate.hashCode()
        result = 31 * result + committerDate.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + committer.hashCode()
        return result
    }

    var taskIds: List<String> = emptyList()
    val isMergeCommit: Boolean
        get() = parents.size > 1

    val isSplitCommit: Boolean
        get() = children.size > 1

    fun addChild(commit: Commit) {
        children = children + listOf(commit)
    }

    fun isAfterInTree(other: Commit): Boolean {
        return parents.contains(other) || parents.any { it.isAfterInTree(other) }
    }
}
