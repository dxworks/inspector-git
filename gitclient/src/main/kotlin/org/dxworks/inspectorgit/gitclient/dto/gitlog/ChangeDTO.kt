package org.dxworks.inspectorgit.gitclient.dto.gitlog

import org.dxworks.inspectorgit.gitclient.dto.ChangeInfoDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType

class ChangeDTO(
        oldFileName: String,
        newFileName: String,
        type: ChangeType,
        parentCommitId: String,
        isBinary: Boolean,
        val hunks: List<HunkDTO>
) : ChangeInfoDTO(
        oldFileName,
        newFileName,
        type,
        parentCommitId,
        isBinary
)
