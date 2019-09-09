package org.dxworks.gitinspector.dto

import org.dxworks.gitinspector.enums.LineOperation

data class LineChangeDTO(val operation: LineOperation, val lineNumber: Int, val content: String)

