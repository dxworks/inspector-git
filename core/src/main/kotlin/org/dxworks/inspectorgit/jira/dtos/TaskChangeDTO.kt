package org.dxworks.inspectorgit.jira.dtos

class TaskChangeDTO(
        val id: String,
        val created: String,
        val userId: String,
        val changedFields: List<String>,
        val items: List<TaskChangeItemDTO>
)