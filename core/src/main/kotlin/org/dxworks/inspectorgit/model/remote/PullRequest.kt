package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.issuetracker.DetailedIssue
import org.dxworks.inspectorgit.remote.dtos.PullRequestCommentDTO
import java.time.ZonedDateTime

class PullRequest(
        val id: Number,
        val title: String,
        val body: String,
        val head: Branch,
        val base: Branch,
        val commitIds: List<String>,
        val createdAt: ZonedDateTime,
        val updatedAt: ZonedDateTime,
        val mergedAt: ZonedDateTime?,
        val closedAt: ZonedDateTime?,
        val state: String,
        val createdBy: RemoteGitAccount,
        val mergedBy: RemoteGitAccount?,
        val reviews: List<PRReview>,
        val comments: List<PullRequestCommentDTO>,
        val project: RemoteGitProject,
        var commits: List<Commit> = emptyList(),
        var issue: DetailedIssue? = null
)