package org.dxworks.inspectorgit.gitclient.dto.gitlog

import org.dxworks.inspectorgit.gitclient.enums.LineOperation

class HunkDTO(lineChanges: List<LineChangeDTO>) {
    var addedLineChanges: List<LineChangeDTO>
    var deletedLineChanges: List<LineChangeDTO>
    var lineChanges: List<LineChangeDTO> = lineChanges
        set(value) {
            field = value
            addedLineChanges = value
            val (addedLineChanges, deletedLineChanges) = value.partition { it.operation == LineOperation.ADD }
            this.addedLineChanges = addedLineChanges
            this.deletedLineChanges = deletedLineChanges
        }

    init {
        addedLineChanges = lineChanges
        val (addedLineChanges, deletedLineChanges) = lineChanges.partition { it.operation == LineOperation.ADD }
        this.addedLineChanges = addedLineChanges
        this.deletedLineChanges = deletedLineChanges
    }

}
