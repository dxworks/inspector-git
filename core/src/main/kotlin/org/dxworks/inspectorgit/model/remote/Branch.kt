package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.issuetracker.DetailedIssue

class Branch(
        commitId: String,
        val label: String,
        ref: String,
        val user: RemoteGitAccount,
        val remoteRepo: RemoteRepo,
        commit: Commit? = null,
        issue: DetailedIssue? = null
) : SimpleBranch(
        commit,
        commitId,
        ref,
        issue
)