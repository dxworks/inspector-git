package org.dxworks.inspectorgit.dto

import org.dxworks.inspectorgit.enums.ChangeType

class ChangeDTO(val oldFileName: String,
                val newFileName: String,
                val type: ChangeType,
                val hunks: List<HunkDTO>,
                var annotatedLines: List<AnnotatedLineDTO> = ArrayList(),
                val otherCommitId: String,
                val isBinary: Boolean)
