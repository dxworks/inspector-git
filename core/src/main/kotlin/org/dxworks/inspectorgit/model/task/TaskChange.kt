package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.jira.dtos.TaskChangeItemDTO
import java.time.ZonedDateTime

class TaskChange(
        val id: String,
        val created: ZonedDateTime,
        val account: TaskAccount,
        val changedFields: List<String>,
        val items: List<TaskChangeItemDTO>
)