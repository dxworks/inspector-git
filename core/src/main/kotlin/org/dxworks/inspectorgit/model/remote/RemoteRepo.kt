package org.dxworks.inspectorgit.model.remote

class RemoteRepo(
        val id: Number,
        val name: String,
        val fullName: String,
        val owner: RemoteGitAccount
)