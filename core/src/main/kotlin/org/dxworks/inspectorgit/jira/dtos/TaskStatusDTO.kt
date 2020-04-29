package org.dxworks.inspectorgit.jira.dtos

data class TaskStatusDTO(
        val id: String,
        val name: String,
        val statusCategory: TaskStatusCategoryDTO
)