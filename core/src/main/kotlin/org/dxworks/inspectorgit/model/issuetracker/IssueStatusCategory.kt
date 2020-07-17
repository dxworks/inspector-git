package org.dxworks.inspectorgit.model.issuetracker

class IssueStatusCategory(
        val project: IssueTrackerProject,
        val key: String,
        val name: String,
        var issueStatuses: List<IssueStatus> = emptyList()
) {
    companion object {
        public val new = "new"
        public val indeterminate = "indeterminate"
        public val done = "done"
    }
}
