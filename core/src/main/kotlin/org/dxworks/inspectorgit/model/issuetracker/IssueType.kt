package org.dxworks.inspectorgit.model.issuetracker

class IssueType(
        val project: IssueTrackerProject,
        val id: String,
        val name: String,
        val description: String,
        val isSubTask: Boolean,
        var issues: List<Issue> = emptyList()
)