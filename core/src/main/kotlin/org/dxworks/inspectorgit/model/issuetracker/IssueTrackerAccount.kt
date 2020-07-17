package org.dxworks.inspectorgit.model.issuetracker

import org.dxworks.inspectorgit.model.Account

class IssueTrackerAccount(
        val self: String,
        val email: String?,
        val key: String?,
        val accountId: String?,
        name: String,
        val avatarUrl: String?,
        project: IssueTrackerProject,
        var issues: Set<Issue> = emptySet()
) : Account(
        name,
        project
) {
    override val id: String
        get() = self
}