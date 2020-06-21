package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit

class Branch(
        commit: Commit?,
        commitId: String,
        val label: String,
        ref: String,
        val user: RemoteGitAccount,
        val remoteRepo: RemoteRepo
) : SimpleBranch(
        commit,
        commitId,
        ref
)