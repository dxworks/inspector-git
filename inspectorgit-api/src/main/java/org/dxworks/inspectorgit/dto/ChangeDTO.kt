package org.dxworks.inspectorgit.dto

import org.dxworks.inspectorgit.enums.ChangeType

class ChangeDTO(val oldFileName: String,
                val newFileName: String,
                val type: ChangeType,
                var hunks: List<HunkDTO> = ArrayList(),
                var annotatedLines: List<AnnotatedLineDTO> = ArrayList(),
                val parentCommitId: String,
                val isBinary: Boolean,
                var isBlame: Boolean)
