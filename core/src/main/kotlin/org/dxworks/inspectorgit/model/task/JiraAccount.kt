package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Project

class JiraAccount(
        name: String,
        override val id: String,
        project: Project,
        var tasks: List<Task> = emptyList()
) : Account(
        name,
        project
)