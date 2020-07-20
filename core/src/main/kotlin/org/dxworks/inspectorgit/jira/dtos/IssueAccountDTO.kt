package org.dxworks.inspectorgit.jira.dtos

class IssueAccountDTO(
        val self: String,
        val name: String,
        val email: String?,
        val key: String?,
        val accountId: String?,
        val avatarUrl: String
)