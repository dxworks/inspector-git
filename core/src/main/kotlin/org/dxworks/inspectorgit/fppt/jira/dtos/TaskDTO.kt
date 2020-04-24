package org.dxworks.inspectorgit.fppt.jira.dtos

class TaskDTO(
        val id: String,
        val self: String,
        val summary: String,
        val status: String,
        val typeId: String,
        val type: String,
        val description: String,
        val parentId: String?,
        val created: String, // example 2020-04-07T08:30:48.589+0300
        val updated: String,
        val subTasks: List<String>,
        val changes: List<TaskChangeDTO>,
        val comments: List<TaskCommentDTO>
)