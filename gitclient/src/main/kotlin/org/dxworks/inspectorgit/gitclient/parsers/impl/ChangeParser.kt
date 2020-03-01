package org.dxworks.inspectorgit.gitclient.parsers.impl

import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.gitclient.parsers.GitParser
import org.slf4j.LoggerFactory

class ChangeParser(private val parentCommitId: String) : GitParser<ChangeDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeParser::class.java)
    }

    override fun parse(lines: List<String>): ChangeDTO {
        val type = extractChangeType(lines)
        val fileName = extractFileName(lines.first())
        LOG.info("Parsing $type change for $fileName")
        return ChangeDTO(
                type = type,
                fileName = fileName,
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
            else -> ChangeType.MODIFY
        }
    }

    private fun extractFileName(diffLine: String): String {
        val namesStartIndex = diffLine.indexOf(" a/") + 3
        val names = diffLine.substring(namesStartIndex)
        val namesParts = names.split(" b/")
        return namesParts.take(namesParts.size / 2).joinToString(" b/")
    }
}
