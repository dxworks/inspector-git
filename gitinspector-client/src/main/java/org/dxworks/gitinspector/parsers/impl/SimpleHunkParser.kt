package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.dto.LineChangeDTO
import org.dxworks.gitinspector.enums.LineOperation
import org.dxworks.gitinspector.parsers.abstracts.HunkParser

class SimpleHunkParser : HunkParser() {
    override fun extractLineChanges(lines: MutableList<String>): List<LineChangeDTO> {
        val (removePair, addPair) = getRemoveAndAddHunkInfo(lines.removeAt(0))
        val (removeStart, removeCount) = removePair
        val (addStart, addCount) = addPair

        val lineChanges: MutableList<LineChangeDTO> = ArrayList()
        var diffLineIndex = 0

        for (i in removeStart until (removeStart + removeCount))
            lineChanges.add(LineChangeDTO(operation = LineOperation.REMOVE, lineNumber = i, content = lines[diffLineIndex++].removePrefix("-")))
        for (i in addStart until (addStart + addCount))
            lineChanges.add(LineChangeDTO(operation = LineOperation.ADD, lineNumber = i, content = lines[diffLineIndex++].removePrefix("+")))

        return lineChanges
    }

    private fun getRemoveAndAddHunkInfo(changeInfoLine: String): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val numbers = changeInfoLine.split("@ ")[1].split(" @")[0]
        val removeAndAddInfo = numbers.split(" ")
        return Pair(getNumbersPair(removeAndAddInfo[0]), getNumbersPair(removeAndAddInfo[1]))
    }
}