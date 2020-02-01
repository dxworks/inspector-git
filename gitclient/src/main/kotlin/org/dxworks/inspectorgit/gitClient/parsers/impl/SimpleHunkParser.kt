package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.dxworks.inspectorgit.gitClient.dto.LineChangeDTO
import org.dxworks.inspectorgit.gitClient.enums.LineOperation
import org.dxworks.inspectorgit.gitClient.parsers.abstracts.HunkParser

class SimpleHunkParser : HunkParser() {
    override fun extractLineChanges(lines: List<String>): List<LineChangeDTO> {
        val (removePair, addPair) = getRemoveAndAddHunkInfo(lines[0])
        val (removeStart, _) = removePair
        val (addStart, _) = addPair

        var removedLineIndex = 0
        var addedLineIndex = 0

        return lines.drop(1). mapNotNull {
            when {
                it.startsWith("-") -> LineChangeDTO(operation = LineOperation.REMOVE, number = removeStart + removedLineIndex++, content = it.removePrefix("-"))
                it.startsWith("+") -> LineChangeDTO(operation = LineOperation.ADD, number = addStart + addedLineIndex++, content = it.removePrefix("+"))
                else -> {
                    removedLineIndex++
                    addedLineIndex++
                    null
                }
            }
        }
    }

    private fun getRemoveAndAddHunkInfo(changeInfoLine: String): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val numbers = changeInfoLine.split("@ ")[1].split(" @")[0]
        val removeAndAddInfo = numbers.split(" ")
        return Pair(getNumbersPair(removeAndAddInfo[0]), getNumbersPair(removeAndAddInfo[1]))
    }
}