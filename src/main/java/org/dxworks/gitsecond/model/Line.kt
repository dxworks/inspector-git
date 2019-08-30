package org.dxworks.gitsecond.model

data class Line(var oldLineNumber: Int, var newLineNumber: Int, var oldContent: String, var newContent: String, var author: Author, var change: Change)
