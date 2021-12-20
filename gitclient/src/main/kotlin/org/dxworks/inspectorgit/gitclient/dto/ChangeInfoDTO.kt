package org.dxworks.inspectorgit.gitclient.dto

import com.fasterxml.jackson.annotation.JsonProperty
import org.dxworks.inspectorgit.gitclient.enums.ChangeType

open class ChangeInfoDTO(
        val oldFileName: String,
        val newFileName: String,
        var type: ChangeType,
        var parentCommitId: String,
        @JsonProperty("b")
        var isBinary: Boolean
)
