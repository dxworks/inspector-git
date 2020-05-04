package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Project

class TaskAccount(
        val self: String,
        val email: String?,
        val key: String?,
        val accountId: String?,
        name: String,
        val avatarUrl: String?,
        project: Project,
        var tasks: Set<Task> = emptySet()
) : Account(
        name,
        project
) {
    override val id: String
        get() = self
}