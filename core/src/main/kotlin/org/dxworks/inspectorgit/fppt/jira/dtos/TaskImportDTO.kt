package org.dxworks.inspectorgit.fppt.jira.dtos

class TaskImportDTO(
        val issueTypes: Map<String, TaskTypeDTO>,
        val users: Map<String, TaskAccountDTO>,
        val issues: List<TaskDTO>
)