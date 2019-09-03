package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.dto.AnnotatedLineDTO
import org.dxworks.gitsecond.dto.HunkDTO

class BlameParser(private val blame: List<String>, lines: MutableList<String>): ChangeParser(lines) {
    override fun extractHunks(): List<HunkDTO> {
        return ArrayList()
    }

    override fun extractAnnotatedLines(): List<AnnotatedLineDTO> {
        TODO("implement extract annotated lines from blame")
    }
}
