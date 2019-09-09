package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.dto.AnnotatedLineDTO
import org.dxworks.gitinspector.dto.HunkDTO
import org.dxworks.gitinspector.parsers.abstracts.ChangeParser

class BlameParser(private val blame: List<String>) : ChangeParser() {
    override fun extractHunks(lines: MutableList<String>): List<HunkDTO> {
        return getHunks(lines).map { MergeHunkParser().parse(it) }
    }

    override fun extractAnnotatedLines(lines: MutableList<String>): List<AnnotatedLineDTO> {
        return blame.filter { it.isNotBlank() }.map {
            val commitIdAndOther = it.split(" (")
            val commitId = commitIdAndOther[0].split(" ")[0]
            val authorAndContent = commitIdAndOther[1].split(") ")
            val content = authorAndContent[1]
            val authorTimeLineNo = authorAndContent[0]
            val lineNumber = authorTimeLineNo.substring(authorTimeLineNo.lastIndexOf(" ") + 1).toInt()
            AnnotatedLineDTO(commitId, lineNumber, content)
        }
    }
}