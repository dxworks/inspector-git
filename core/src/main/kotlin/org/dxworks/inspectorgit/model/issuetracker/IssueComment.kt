package org.dxworks.inspectorgit.model.issuetracker

import java.time.ZonedDateTime

class IssueComment(
        val project: IssueTrackerProject,
        val created: ZonedDateTime,
        val createdBy: IssueTrackerAccount,
        val updated: ZonedDateTime?,
        val updatedBy: IssueTrackerAccount?,
        val body: String
)
