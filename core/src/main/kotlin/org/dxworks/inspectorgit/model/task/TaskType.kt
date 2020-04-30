package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Project

class TaskType(
        val project: Project,
        val id: String,
        val name: String,
        val description: String,
        val isSubTask: Boolean,
        var tasks: List<Task> = emptyList()
)