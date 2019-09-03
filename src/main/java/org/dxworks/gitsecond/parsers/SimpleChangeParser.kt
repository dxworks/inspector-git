package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.dto.AnnotatedLineDTO
import org.dxworks.gitsecond.dto.ChangeDTO
import org.dxworks.gitsecond.dto.HunkDTO
import org.dxworks.gitsecond.model.ChangeType

class SimpleChangeParser(lines: MutableList<String>): ChangeParser(lines) {
    override fun extractAnnotatedLines(): List<AnnotatedLineDTO> {
        return ArrayList()
    }

    override fun extractHunks(): List<HunkDTO> {
        return if (lines.isNotEmpty()) {
            getHunks().map { HunkParser(it).hunk }
        } else ArrayList()
    }
}
