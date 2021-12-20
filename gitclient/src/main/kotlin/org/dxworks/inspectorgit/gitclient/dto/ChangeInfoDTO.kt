package org.dxworks.inspectorgit.gitclient.dto

import org.dxworks.inspectorgit.gitclient.enums.ChangeType

open class ChangeInfoDTO(
        val oldFileName: String,
        val newFileName: String,
        var type: ChangeType,
        var parentCommitId: String,
        var isBinary: Boolean
)
