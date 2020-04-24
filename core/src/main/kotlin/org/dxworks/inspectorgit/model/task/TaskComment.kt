package org.dxworks.inspectorgit.model.task

import java.time.ZonedDateTime

class TaskComment(
        val created: ZonedDateTime,
        val createdBy: TaskAccount,
        val updated: ZonedDateTime?,
        val updatedBy: TaskAccount?,
        val body: String
)
