package org.dxworks.inspectorgit.jira.dtos

class TaskCommentDTO(
        val created: String,
        val userId: String,
        val updated: String?,
        val updateUserId: String?,
        val body: String
)