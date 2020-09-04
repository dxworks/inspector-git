package org.dxworks.inspectorgit.model.git

import org.dxworks.inspectorgit.gitclient.enums.LineOperation

data class LineChange(val operation: LineOperation, val lineNumber: Int, val commit: Commit)

