package org.dxworks.inspectorgit.model.task

class TaskType(
        val id: String,
        val name: String,
        val description: String,
        val isSubTask: Boolean,
        var tasks: List<Task> = emptyList()
)