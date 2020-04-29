package org.dxworks.inspectorgit.jira.dtos

class TaskTypeDTO(
        var id: String,
        val name: String,
        val description: String,
        val isSubTask: Boolean
)