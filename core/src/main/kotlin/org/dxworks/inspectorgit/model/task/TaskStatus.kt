package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Project


class TaskStatus(
        val project: Project,
        val id: String,
        val name: String,
        val category: TaskStatusCategory,
        var tasks: List<Task> = emptyList()
)