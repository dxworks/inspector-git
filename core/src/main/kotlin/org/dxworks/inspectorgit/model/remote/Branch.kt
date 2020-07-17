package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.issuetracker.DetailedIssue

class Branch(
        commit: Commit?,
        commitId: String,
        val label: String,
        ref: String,
        val user: RemoteGitAccount,
        val remoteRepo: RemoteRepo,
        issue: DetailedIssue?
) : SimpleBranch(
        commit,
        commitId,
        ref,
        issue
)