package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.task.DetailedTask

class Branch(
        commit: Commit?,
        commitId: String,
        val label: String,
        ref: String,
        val user: RemoteGitAccount,
        val remoteRepo: RemoteRepo,
        task: DetailedTask?
) : SimpleBranch(
        commit,
        commitId,
        ref,
        task
)