package org.dxworks.inspectorgit.parsers.abstracts

import org.dxworks.inspectorgit.dto.HunkDTO
import org.dxworks.inspectorgit.dto.LineChangeDTO
import org.dxworks.inspectorgit.parsers.GitParser

abstract class HunkParser : GitParser<HunkDTO> {

    override fun parse(lines: MutableList<String>): HunkDTO {
        return HunkDTO(extractLineChanges(lines))
    }

    protected abstract fun extractLineChanges(lines: MutableList<String>): List<LineChangeDTO>

    protected fun getNumbersPair(info: String): Pair<Int, Int> {
        val numbers = info.substring(1)
        val lineNumberAndCount = numbers.split(",")
        val lineNumber = Integer.parseInt(lineNumberAndCount[0])
        val lineCount = if (lineNumberAndCount.size < 2) 1 else Integer.parseInt(lineNumberAndCount[1])
        return Pair(lineNumber, lineCount)
    }
}