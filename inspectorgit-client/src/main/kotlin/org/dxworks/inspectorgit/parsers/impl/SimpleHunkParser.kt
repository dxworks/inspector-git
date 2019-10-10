package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.LineChangeDTO
import org.dxworks.inspectorgit.enums.LineOperation
import org.dxworks.inspectorgit.parsers.abstracts.HunkParser

class SimpleHunkParser : HunkParser() {
    override fun extractLineChanges(lines: List<String>): List<LineChangeDTO> {
        val (removePair, addPair) = getRemoveAndAddHunkInfo(lines[0])
        val (removeStart, removeCount) = removePair
        val (addStart, addCount) = addPair

        val lineChanges: MutableList<LineChangeDTO> = ArrayList()
        var diffLineIndex = 1

        for (i in removeStart until (removeStart + removeCount))
            lineChanges.add(LineChangeDTO(operation = LineOperation.REMOVE, number = i, content = lines[diffLineIndex++].removePrefix("-")))
        for (i in addStart until (addStart + addCount))
            lineChanges.add(LineChangeDTO(operation = LineOperation.ADD, number = i, content = lines[diffLineIndex++].removePrefix("+")))

        return lineChanges
    }

    private fun getRemoveAndAddHunkInfo(changeInfoLine: String): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val numbers = changeInfoLine.split("@ ")[1].split(" @")[0]
        val removeAndAddInfo = numbers.split(" ")
        return Pair(getNumbersPair(removeAndAddInfo[0]), getNumbersPair(removeAndAddInfo[1]))
    }
}