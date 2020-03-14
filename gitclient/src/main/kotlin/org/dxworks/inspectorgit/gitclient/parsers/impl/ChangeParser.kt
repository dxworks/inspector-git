package org.dxworks.inspectorgit.gitclient.parsers.impl

import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.gitclient.parsers.GitParser
import org.dxworks.inspectorgit.utils.devNull
import org.slf4j.LoggerFactory

class ChangeParser(private val parentCommitId: String) : GitParser<ChangeDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeParser::class.java)
    }

    override fun parse(lines: List<String>): ChangeDTO {
        val type = extractChangeType(lines)
        val (oldFileName, newFileName) = extractFileNames(lines, type)
        LOG.info("Parsing $type change for $oldFileName -> $newFileName")
        return ChangeDTO(
                type = type,
                oldFileName = oldFileName,
                newFileName = newFileName,
                parentCommitId = parentCommitId,
                hunks = extractHunks(lines).map { HunkParser().parse(it) },
                isBinary = lines.any { it.startsWith("Binary files") })
    }

    private fun extractHunks(lines: List<String>): List<List<String>> {
        val hunks: MutableList<MutableList<String>> = ArrayList()
        var currentHunkLines: MutableList<String> = ArrayList()
        LOG.info("Extracting hunks")
        val firstHunkIndex = lines.indexOfFirst { it.startsWith("@") }
        return if (firstHunkIndex == -1)
            emptyList()
        else {
            lines.subList(firstHunkIndex, lines.size).forEach {
                if (it.startsWith("@")) {
                    currentHunkLines = ArrayList()
                    hunks.add(currentHunkLines)
                }
                if (!it.startsWith("\\"))
                    currentHunkLines.add(it)
            }
            LOG.info("Found ${hunks.size} hunks")
            hunks
        }
    }

    private fun extractChangeType(lines: List<String>): ChangeType {
        return when {
            lines.find { it.startsWith("new file mode") } != null -> ChangeType.ADD
            lines.find { it.startsWith("deleted file mode") } != null -> ChangeType.DELETE
            lines.find { it.startsWith("similarity index") } != null -> ChangeType.RENAME
            else -> ChangeType.MODIFY
        }
    }

    private fun extractFileNames(lines: List<String>, type: ChangeType): Pair<String, String> {
        val oldFilePrefix = "--- a/"
        val newFilePrefix = "+++ b/"
        val oldFileName = if (type == ChangeType.ADD) devNull else lines.find { it.startsWith(oldFilePrefix) }!!.removePrefix(oldFilePrefix)
        val newFileName = if (type == ChangeType.DELETE) devNull else lines.find { it.startsWith(newFilePrefix) }!!.removePrefix(newFilePrefix)
        return Pair(oldFileName, newFileName)
    }
}
