package org.dxworks.inspectorgit.pullrequests.miners.bitbucket.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthorDTO(
        val uuid: String,
        val username: String,
        @JsonProperty("display_name")
        val displayName: String
)
