package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.Account

class RemoteGitAccount(
        val login: String,
        val url: String,
        val email: String? = null,
        val avatarUrl: String? = null,
        val userName: String? = null,
        project: RemoteGitProject,
        var pullRequests: Set<PullRequest> = emptySet(),
        var openedPullRequests: Set<PullRequest> = emptySet()
) : Account(login, project) {
    override val id: String
        get() = url
}
