package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Project

class RemoteGitAccount(
        val login: String,
        val url: String,
        val email: String?,
        val avatarUrl: String?,
        val userName: String?,
        project: Project,
        var pullRequests: Set<PullRequest> = emptySet(),
        var openedPullRequests: Set<PullRequest> = emptySet()
) : Account(login, project) {
    override val id: String
        get() = url
}