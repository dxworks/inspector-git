package org.dxworks.inspectorgit.model.issuetracker

import org.dxworks.inspectorgit.jira.dtos.TaskChangeItemDTO
import java.time.ZonedDateTime

class IssueChange(
        val id: String,
        val project: IssueTrackerProject,
        val created: ZonedDateTime,
        val account: IssueTrackerAccount,
        val changedFields: List<String>,
        val items: List<TaskChangeItemDTO>
) {
    fun changedField(fieldName: String) = changedFields.contains(fieldName)
    fun getItemForField(fieldName: String) = items.find { it.field == fieldName }
}