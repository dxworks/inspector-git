package org.dxworks.inspectorgit.gitclient.extractors.impl

import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.iglog.LineOperationsMeta
import org.dxworks.inspectorgit.gitclient.extractors.MetaExtractor

class LineOperationsMetaExtractor : MetaExtractor<LineOperationsMeta>() {
    private val splitter = "|"
    private val rangesSplitter = " "
    private val rangeMarker = ":"
    private val pairZero = Pair(0, 0)

    override val linePrefix: String
        get() = "+/-"

    override fun extract(hunkDTO: HunkDTO): String {
        val deletedLines = hunkDTO.deletedLineChanges
        val deleteRanges = extractRanges(deletedLines)

        val addedLines = hunkDTO.addedLineChanges
        val addRanges = extractRanges(addedLines)

        return "${getFormattedRanges(addRanges)}$splitter${getFormattedRanges(deleteRanges)}"
    }

    private fun extractRanges(lines: List<LineChangeDTO>): List<Pair<Int, Int>> {
        return if (lines.isNotEmpty()) {
            if (allLinesAreConsecutive(lines)) listOf(Pair(lines.first().number, lines.last().number))
            else {
                extractNumberRanges(lines.map { it.number })
            }
        } else listOf(pairZero)
    }

    private fun extractNumberRanges(numbers: List<Int>): List<Pair<Int, Int>> {
        var rangeStart = numbers[0]
        var rangeEnd = numbers[0]
        val ranges: MutableList<Pair<Int, Int>> = ArrayList()

        for (i in numbers.indices) {
            if (i == numbers.size - 1 || numbers[i] + 1 != numbers[i + 1]) {
                ranges.add(Pair(rangeStart, rangeEnd))
                if (i != numbers.size - 1) {
                    rangeStart = numbers[i + 1]
                    rangeEnd = numbers[i + 1]
                }
            } else
                rangeEnd++
        }

        return ranges
    }

    private fun allLinesAreConsecutive(lines: List<LineChangeDTO>): Boolean {
        val min = lines.first().number - 1
        val sum = lines.map { it.number - min }.sum()
        val n = lines.size
        return n * (n + 1) / 2 == sum
    }

    private fun getFormattedRanges(ranges: List<Pair<Int, Int>>): String {
        return ranges.joinToString(rangesSplitter) { getFormattedRange(it) }
    }

    private fun getFormattedRange(range: Pair<Int, Int>): String {
        return when {
            range == pairZero -> "0"
            range.first == range.second -> "${range.first}"
            else -> "${range.first}:${range.second}"
        }
    }

    override fun parse(line: String): LineOperationsMeta {
        val split = line.split(splitter)
        val addedLinesRanges = split[0]
        val deletedLinesRanges = split[1]
        return LineOperationsMeta(parseRanges(addedLinesRanges), parseRanges(deletedLinesRanges))
    }

    private fun parseRanges(lineRanges: String): List<Pair<Int, Int>> {
        val ranges = lineRanges.split(rangesSplitter)
        return ranges.mapNotNull {
            val range = it.split(rangeMarker)
            if (range.size == 1) {
                if (range[0] != "0") {
                    val lineNumber = range[0].toInt()
                    Pair(lineNumber, lineNumber)
                } else null
            } else Pair(range[0].toInt(), range[1].toInt())
        }
    }
}