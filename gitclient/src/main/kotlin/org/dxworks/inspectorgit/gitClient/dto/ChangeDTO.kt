package org.dxworks.inspectorgit.gitClient.dto

import org.dxworks.inspectorgit.gitClient.enums.ChangeType

class ChangeDTO(val oldFileName: String,
                val newFileName: String,
                val type: ChangeType,
                var hunks: List<HunkDTO> = ArrayList(),
                var annotatedLines: List<AnnotatedLineDTO> = ArrayList(),
                val parentCommitId: String,
                val isBinary: Boolean,
                var isBlame: Boolean)
