package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.dto.HunkDTO
import org.dxworks.gitsecond.dto.LineChangeDTO
import org.dxworks.gitsecond.model.LineOperation

class HunkParser(val lines: MutableList<String>) {

    val hunk: HunkDTO

    init {
        val (removePair, addPair) = getRemoveAndAddHunkInfo(lines.removeAt(0))
        hunk = HunkDTO(
                lineChanges = extractLineChanges(removePair, addPair))
    }

    private fun extractLineChanges(removePair: Pair<Int, Int>, addPair: Pair<Int, Int>): List<LineChangeDTO> {
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

    private fun getNumbersPair(info: String): Pair<Int, Int> {
        val numbers = info.substring(1)
        val lineNumberAndCount = numbers.split(",")
        val lineNumber = Integer.parseInt(lineNumberAndCount[0])
        val lineCount = if (lineNumberAndCount.size < 2) 1 else Integer.parseInt(lineNumberAndCount[1])
        return Pair(lineNumber, lineCount)
    }
}
