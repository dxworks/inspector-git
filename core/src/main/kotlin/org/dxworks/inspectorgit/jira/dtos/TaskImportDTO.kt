package org.dxworks.inspectorgit.jira.dtos

class TaskImportDTO(
        val issueTypes: List<TaskTypeDTO>,
        val users: List<TaskAccountDTO>,
        val issues: List<TaskDTO>,
        val issueStatuses: List<TaskStatusDTO>
)