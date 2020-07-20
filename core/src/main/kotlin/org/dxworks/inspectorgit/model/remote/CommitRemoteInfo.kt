package org.dxworks.inspectorgit.model.remote

class CommitRemoteInfo(
        val commitId: String,
        val author: RemoteGitAccount?,
        val committer: RemoteGitAccount?
)