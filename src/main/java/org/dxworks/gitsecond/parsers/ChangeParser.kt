package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.model.ChangeType

class ChangeParser(private val lines: MutableList<String>) {
    val oldFileName: String
    val newFileName: String
    val changeType: ChangeType
    val hunkParsers: List<HunkParser>

    private val oldFileNameLinePrefix = "--- "
    private val newFileNameLinePrefix = "+++ "


    init {
        lines.removeAt(0)
        changeType = extractChangeType()
        val (oldFileName, newFileName) = extractFileNames()

        this.oldFileName = oldFileName
        this.newFileName = newFileName
        hunkParsers = createHunkParsers()
    }

    private fun extractChangeType(): ChangeType {
        val changeTypeLine = lines.removeAt(0)
        return if (changeTypeLine.startsWith("new file mode")) {
            lines.removeAt(0)
            ChangeType.ADD
        } else if (changeTypeLine.startsWith("deleted file mode")) {
            lines.removeAt(0)
            ChangeType.DELETE
        } else if (changeTypeLine.startsWith("similarity index")) {
            lines.removeAt(0)
            lines.removeAt(0)
            lines.removeAt(0)
            ChangeType.RENAME
        } else ChangeType.MODIFY
    }

    private fun extractFileNames(): Pair<String, String> {
        val oldFileNameLineIndex = lines.indexOfFirst { it.startsWith(oldFileNameLinePrefix) }
        return Pair(first = lines.removeAt(oldFileNameLineIndex).removePrefix(oldFileNameLinePrefix).removePrefix("a"),
                second = lines.removeAt(oldFileNameLineIndex).removePrefix(newFileNameLinePrefix).removePrefix("b"))
    }

    private fun createHunkParsers(): List<HunkParser> {
        return if (lines.isNotEmpty()) {
            val hunks: MutableList<MutableList<String>> = ArrayList()
            var currentHunkLines: MutableList<String> = ArrayList()
            lines.forEach {
                if (it.startsWith("@")) {
                    currentHunkLines = ArrayList()
                    hunks.add(currentHunkLines)
                }
                currentHunkLines.add(it)
            }
            hunks.map { HunkParser(it) }
        } else ArrayList()
    }
}
