package org.dxworks.gitsecond.dto

import org.dxworks.gitsecond.model.LineOperation

data class LineChangeDTO(val operation: LineOperation, val lineNumber: Int, val content: String)

