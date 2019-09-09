package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.dto.AnnotatedLineDTO
import org.dxworks.gitinspector.dto.HunkDTO
import org.dxworks.gitinspector.parsers.abstracts.ChangeParser

class SimpleChangeParser : ChangeParser() {
    override fun extractAnnotatedLines(lines: MutableList<String>): List<AnnotatedLineDTO> {
        return emptyList()
    }

    override fun extractHunks(lines: MutableList<String>): List<HunkDTO> {
        return if (lines.isNotEmpty()) {
            getHunks(lines).map { SimpleHunkParser().parse(it) }
        } else emptyList()
    }
}
