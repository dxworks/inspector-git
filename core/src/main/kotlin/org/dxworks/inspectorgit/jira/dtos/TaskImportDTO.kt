package org.dxworks.inspectorgit.jira.dtos

class TaskImportDTO(
        val issueTypes: List<IssueTypeDTO>,
        val users: List<IssueAccountDTO>,
        val issues: List<IssueDTO>,
        val issueStatuses: List<IssueStatusDTO>
)