package org.dxworks.inspectorgit.jira.dtos

class IssueTrackerImportDTO(
        val issueTypes: List<IssueTypeDTO>,
        val users: List<IssueAccountDTO>,
        val issues: List<IssueDTO>,
        val issueStatuses: List<IssueStatusDTO>
)