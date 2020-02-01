package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.dxworks.inspectorgit.gitClient.dto.LineChangeDTO
import org.dxworks.inspectorgit.gitClient.enums.LineOperation
import org.dxworks.inspectorgit.gitClient.parsers.abstracts.HunkParser

class MergeHunkParser(private val parentIndex: Int, private val numberOfParents: Int) : HunkParser() {
    private val spaces = " ".repeat(numberOfParents)
    private val minus = '-'
    private val plus = "+"

    override fun extractLineChanges(lines: List<String>): List<LineChangeDTO> {
        val (fromFileRange, toFileRange) = getFileRanges(lines)
        return getFromLineChanges(fromFileRange, getFromLines(lines)) + getToLineChanges(toFileRange, getToLines(lines))
    }

    private fun getFromLineChanges(fromFileRange: Pair<Int, Int>, lines: List<String>): List<LineChangeDTO> {
        val fromFileRangeStart = fromFileRange.first
        return lines.mapIndexedNotNull { i, line ->
            if (line[parentIndex] == minus)
                LineChangeDTO(LineOperation.REMOVE, fromFileRangeStart + i, line.substring(numberOfParents))
            else null
        }
    }

    private fun getToLineChanges(toFileRange: Pair<Int, Int>, lines: List<String>): List<LineChangeDTO> {
        val toFileRangeStart = toFileRange.first
        return lines.mapIndexedNotNull { i, line ->
            if (line[parentIndex].toString() == plus)
                LineChangeDTO(LineOperation.ADD, toFileRangeStart + i, line.substring(numberOfParents))
            else null
        }
    }

    private fun getFromLines(lines: List<String>): List<String> {
        val pluses = plus.repeat(numberOfParents)
        return lines.filter {
            it.startsWith(spaces) ||
                    it[parentIndex] == minus ||
                    it.startsWith(pluses.replaceRange(parentIndex, parentIndex + 1, " "))

        }
    }

    private fun getToLines(lines: List<String>): List<String> {
        return lines.filter {
            it.startsWith(spaces) ||
                    it.substring(0, numberOfParents).contains(plus)
        }
    }

    private fun getFileRanges(lines: List<String>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val fileRangeNumbers = lines[0].split("@ ")[1].split(" @")[0]
        val fileRangeSplitNumbers = fileRangeNumbers.split(" ")
        val fromFileRange = getNumbersPair(fileRangeSplitNumbers[parentIndex])
        val toFileRange = getNumbersPair(fileRangeSplitNumbers.last())
        return Pair(fromFileRange, toFileRange)
    }
}
