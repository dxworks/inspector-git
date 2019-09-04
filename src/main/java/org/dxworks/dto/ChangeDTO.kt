package org.dxworks.dto

import org.dxworks.gitsecond.model.ChangeType

class ChangeDTO(val oldFileName: String,
                val newFileName: String,
                val type: ChangeType,
                val hunks: List<HunkDTO>,
                val annotatedLines: List<AnnotatedLineDTO>)
