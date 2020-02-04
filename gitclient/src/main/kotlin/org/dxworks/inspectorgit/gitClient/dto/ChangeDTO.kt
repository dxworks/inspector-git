package org.dxworks.inspectorgit.gitClient.dto

import org.dxworks.inspectorgit.gitClient.enums.ChangeType

class ChangeDTO(val fileName: String,
                var type: ChangeType,
                val hunks: List<HunkDTO>,
                var parentCommitId: String,
                val isBinary: Boolean)
