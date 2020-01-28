package org.dxworks.inspectorgit.gitClient.dto

import org.dxworks.inspectorgit.gitClient.enums.ChangeType

class ChangeDTO(var oldFileName: String,
                val newFileName: String,
                var type: ChangeType,
                var hunks: List<HunkDTO> = ArrayList(),
                val parentCommitId: String,
                val isBinary: Boolean)
