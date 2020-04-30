package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.jira.dtos.TaskChangeItemDTO
import org.dxworks.inspectorgit.model.Project
import java.time.ZonedDateTime

class TaskChange(
        val id: String,
        val project: Project,
        val created: ZonedDateTime,
        val account: TaskAccount,
        val changedFields: List<String>,
        val items: List<TaskChangeItemDTO>
) {
    fun changedField(fieldName: String) = changedFields.contains(fieldName)
    fun getItemForField(fieldName: String) = items.find { it.field == fieldName }
}