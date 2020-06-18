package org.dxworks.inspectorgit.model.remote

import java.time.ZonedDateTime

class PRReview(
        val user: RemoteGitAccount,
        val status: String,
        val body: String,
        val date: ZonedDateTime
)
