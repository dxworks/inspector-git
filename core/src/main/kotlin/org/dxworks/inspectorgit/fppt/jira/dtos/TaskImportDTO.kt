package org.dxworks.inspectorgit.fppt.jira.dtos

class TaskImportDTO(
        val issueTypes: List<TaskTypeDTO>,
        val users: List<TaskAccountDTO>,
        val issues: List<TaskDTO>
)