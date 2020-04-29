package org.dxworks.inspectorgit.model.task

class TaskStatusCategory(
        val key: String,
        val name: String,
        var taskStatuses: List<TaskStatus> = emptyList()
)
