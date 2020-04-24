package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Project

class TaskAccount(
        override val id: String,
        name: String,
        val avatarUrl: String,
        project: Project,
        var tasks: Set<Task> = emptySet()
) : Account(
        name,
        project
)