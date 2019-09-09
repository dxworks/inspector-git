package org.dxworks.gitinspector.model

import org.dxworks.gitinspector.enums.LineOperation

data class LineChange(var operation: LineOperation, var lineNumber: Int, var content: String)
