package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Project

class TaskAccount(
        override val id: String,
        name: String,
        val avatarUrl: String,
        project: Project,
        var tasks: Set<Task> = emptySet() //TODO: list of task relation task relation has strength, task and account
) : Account(
        name,
        project
)