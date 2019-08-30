package org.dxworks.gitsecond.model

data class LineChange(var operation: LineOperation, var lineNumber: Int, var content: String)
