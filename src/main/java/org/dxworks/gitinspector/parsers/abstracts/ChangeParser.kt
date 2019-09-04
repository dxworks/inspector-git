package org.dxworks.gitinspector.parsers.abstracts

import org.dxworks.dto.AnnotatedLineDTO
import org.dxworks.dto.ChangeDTO
import org.dxworks.dto.HunkDTO
import org.dxworks.gitsecond.model.ChangeType
import org.dxworks.gitinspector.parsers.GitParser

abstract class ChangeParser : GitParser<ChangeDTO> {
    private val oldFileNameLinePrefix = "--- "
    private val newFileNameLinePrefix = "+++ "

    override fun parse(lines: MutableList<String>): ChangeDTO {
        lines.removeAt(0)
        val (oldFileName, newFileName) = extractFileNames(lines)
        return ChangeDTO(
                type = extractChangeType(lines),
                oldFileName = oldFileName,
                newFileName = newFileName,
                hunks = extractHunks(lines),
                annotatedLines = extractAnnotatedLines(lines))
    }

    abstract fun extractHunks(lines: MutableList<String>): List<HunkDTO>

    abstract fun extractAnnotatedLines(lines: MutableList<String>): List<AnnotatedLineDTO>

    protected fun getHunks(lines: MutableList<String>): List<MutableList<String>> {
        val hunks: MutableList<MutableList<String>> = ArrayList()
        var currentHunkLines: MutableList<String> = ArrayList()
        lines.forEach {
            if (it.startsWith("@")) {
                currentHunkLines = ArrayList()
                hunks.add(currentHunkLines)
            }
            currentHunkLines.add(it)
        }
        return hunks
    }

    private fun extractChangeType(lines: MutableList<String>): ChangeType {
        val changeTypeLine = lines.removeAt(0)
        return when {
            changeTypeLine.startsWith("new file mode") -> {
                lines.removeAt(0)
                ChangeType.ADD
            }
            changeTypeLine.startsWith("deleted file mode") -> {
                lines.removeAt(0)
                ChangeType.DELETE
            }
            changeTypeLine.startsWith("similarity index") -> {
                lines.removeAt(0)
                lines.removeAt(0)
                lines.removeAt(0)
                ChangeType.RENAME
            }
            else -> ChangeType.MODIFY
        }
    }

    private fun extractFileNames(lines: MutableList<String>): Pair<String, String> {
        val oldFileNameLineIndex = lines.indexOfFirst { it.startsWith(oldFileNameLinePrefix) }
        return Pair(first = lines.removeAt(oldFileNameLineIndex).removePrefix(oldFileNameLinePrefix).removePrefix("a"),
                second = lines.removeAt(oldFileNameLineIndex).removePrefix(newFileNameLinePrefix).removePrefix("b"))
    }
}