package org.dxworks.gitinspector.parsers.impl

import org.dxworks.dto.LineChangeDTO
import org.dxworks.gitinspector.parsers.abstracts.HunkParser

class MergeHunkParser : HunkParser() {
    override fun extractLineChanges(lines: MutableList<String>): List<LineChangeDTO> {
        return emptyList()
    }
}