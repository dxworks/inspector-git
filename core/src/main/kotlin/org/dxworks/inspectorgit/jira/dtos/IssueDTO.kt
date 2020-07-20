package org.dxworks.inspectorgit.jira.dtos

class IssueDTO(
        val key: String,
        val self: String,
        val summary: String,
        val status: IssueStatusDTO,
        val typeId: String,
        val type: String,
        val description: String?,
        val parent: String?,
        val created: String, // example 2020-04-07T08:30:48.589+0300
        val updated: String,
        val creatorId: String?,
        val reporterId: String?,
        val assigneeId: String?,
        val priority: String,
        val subTasks: List<String>,
        val changes: List<TaskChangeDTO>,
        val comments: List<TaskCommentDTO>,
        val timeSpent: Long?,
        val timeEstimate: Long?,
        val customFields: Map<String, Any>
)