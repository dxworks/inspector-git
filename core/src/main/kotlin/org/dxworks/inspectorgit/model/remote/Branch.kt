package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit

class Branch(
        val commit: Commit,
        val label: String,
        val ref: String,
        val user: RemoteGitAccount,
        val remoteRepo: RemoteRepo
)