package org.dxworks.inspectorgit.model.issuetracker


class IssueStatus(
        val project: IssueTrackerProject,
        val id: String,
        val name: String,
        val category: IssueStatusCategory,
        var issues: List<Issue> = emptyList()
)