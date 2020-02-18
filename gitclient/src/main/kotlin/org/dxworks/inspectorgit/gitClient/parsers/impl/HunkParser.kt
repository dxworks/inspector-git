package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.dxworks.inspectorgit.gitClient.dto.HunkDTO
import org.dxworks.inspectorgit.gitClient.dto.LineChangeDTO
import org.dxworks.inspectorgit.gitClient.enums.LineOperation
import org.dxworks.inspectorgit.gitClient.parsers.GitParser

class HunkParser : GitParser<HunkDTO> {
    override fun parse(lines: List<String>): HunkDTO {
        return HunkDTO(extractLineChanges(lines))
    }

    private fun extractLineChanges(lines: List<String>): List<LineChangeDTO> {
        val (fromLineNumber, toLineNumber) = getFromAndToLineNumbers(lines[0])

        var deletedLineIndex = 0
        var addedLineIndex = 0

        return lines.drop(1).mapNotNull {
            when {
                it.startsWith("-") -> LineChangeDTO(operation = LineOperation.DELETE, number = fromLineNumber + deletedLineIndex++, content = it.removePrefix("-"))
                it.startsWith("+") -> LineChangeDTO(operation = LineOperation.ADD, number = toLineNumber + addedLineIndex++, content = it.removePrefix("+"))
                else -> {
                    deletedLineIndex++
                    addedLineIndex++
                    null
                }
            }
        }
    }

    private fun getFromAndToLineNumbers(hunkInfoLine: String): Pair<Int, Int> {
        val numbers = hunkInfoLine.split("@ ")[1].split(" @")[0]
        val deleteAndAddInfo = numbers.split(" ")
        return Pair(getStartLineNumber(deleteAndAddInfo[0]), getStartLineNumber(deleteAndAddInfo[1]))
    }

    private fun getStartLineNumber(info: String): Int {
        val numbers = info.substring(1)
        val lineNumberAndCount = numbers.split(",")
        return Integer.parseInt(lineNumberAndCount[0])
    }
}