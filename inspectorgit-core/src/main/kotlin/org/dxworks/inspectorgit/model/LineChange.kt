package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.enums.LineOperation

data class LineChange(var operation: LineOperation, var lineNumber: Int, var content: String)
