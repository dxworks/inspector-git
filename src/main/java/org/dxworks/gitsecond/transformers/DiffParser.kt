package org.dxworks.gitsecond.transformers

import org.dxworks.gitsecond.model.LineChange
import org.dxworks.gitsecond.model.LineOperation
import java.util.*
import kotlin.collections.ArrayList

class DiffParser(content: String) {
    private val lines: List<String> = Collections.unmodifiableList(content.split("\n"))

    val lineChanges: MutableList<LineChange> = ArrayList()

    init {
        lines.filter { it.startsWith("@@") }.forEach {
            val (removePair, addPair) = getRemoveAndAddHunkInfo(it)
            val (removeStart, removeSize) = removePair
            val (addStart, addSize) = addPair

            val firstLineIndex = lines.indexOf(it) + 1

            var diffLineIndex = firstLineIndex
            for (i in removeStart until (removeStart + removeSize))
                lineChanges.add(LineChange(operation = LineOperation.REMOVE, lineNumber = i, content = lines[diffLineIndex++].removePrefix("-")))
            for (i in addStart until (addStart + addSize))
                lineChanges.add(LineChange(operation = LineOperation.ADD, lineNumber = i, content = lines[diffLineIndex++].removePrefix("+")))
        }

    }

    private fun getRemoveAndAddHunkInfo(it: String): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        val line = it.replace("@", "").trim()
        val info = line.split(" ")

        val removeInfo = info[0].substring(1)
        val removeNumbers = removeInfo.split(",")
        val removeStart = Integer.parseInt(removeNumbers[0])
        val removeSize = if (removeNumbers.size < 2) 1 else Integer.parseInt(removeNumbers[1])
        val removePair = Pair(removeStart, removeSize)

        val addInfo = info[1].substring(1)
        val addNumbers = addInfo.split(",")
        val addStart = Integer.parseInt(addNumbers[0])
        val addSize = if (addNumbers.size < 2) 1 else Integer.parseInt(addNumbers[1])
        val addPair = Pair(addStart, addSize)
        return Pair(removePair, addPair)
    }
}