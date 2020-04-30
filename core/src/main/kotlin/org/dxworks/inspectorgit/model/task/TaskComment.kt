package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Project
import java.time.ZonedDateTime

class TaskComment(
        val project: Project,
        val created: ZonedDateTime,
        val createdBy: TaskAccount,
        val updated: ZonedDateTime?,
        val updatedBy: TaskAccount?,
        val body: String
)
