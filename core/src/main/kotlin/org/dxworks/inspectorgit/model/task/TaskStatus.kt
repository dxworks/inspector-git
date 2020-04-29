package org.dxworks.inspectorgit.model.task


class TaskStatus(
        val id: String,
        val name: String,
        val category: TaskStatusCategory,
        var tasks: List<Task> = emptyList()
)