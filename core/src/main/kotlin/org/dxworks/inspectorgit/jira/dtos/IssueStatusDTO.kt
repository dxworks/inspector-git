package org.dxworks.inspectorgit.jira.dtos

data class IssueStatusDTO(
        val id: String,
        val name: String,
        val statusCategory: TaskStatusCategoryDTO
)