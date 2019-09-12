package org.dxworks.gitinspector.dto

import org.dxworks.gitinspector.enums.ChangeType

class ChangeDTO(val oldFileName: String,
                val newFileName: String,
                val type: ChangeType,
                val hunks: List<HunkDTO>,
                var annotatedLines: List<AnnotatedLineDTO> = ArrayList(),
                val otherCommitId: String,
                val isBinary: Boolean)
