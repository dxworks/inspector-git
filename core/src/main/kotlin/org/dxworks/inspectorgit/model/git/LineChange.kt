package org.dxworks.inspectorgit.model.git

data class LineChange(val operation: LineOperation, val lineNumber: Int, val commit: Commit)
