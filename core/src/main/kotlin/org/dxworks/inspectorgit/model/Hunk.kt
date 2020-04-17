package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitclient.enums.LineOperation

class Hunk(val lineChanges: List<LineChange>) {
    val deletedLines: List<LineChange>
    val addedLines: List<LineChange>

    init {
        val (deletedLines, addedLines) = lineChanges.partition { it.operation == LineOperation.DELETE }
        this.deletedLines = deletedLines
        this.addedLines = addedLines
    }
}
