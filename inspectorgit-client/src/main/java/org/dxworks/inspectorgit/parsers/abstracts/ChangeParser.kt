package org.dxworks.inspectorgit.parsers.abstracts

import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.parsers.GitParser
import org.dxworks.inspectorgit.utils.devNull
import org.slf4j.LoggerFactory

@Slf4j
abstract class ChangeParser(private val parentCommitId: String) : GitParser<ChangeDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeParser::class.java)
    }

    open val isBlameParser = false

    override fun parse(lines: List<String>): ChangeDTO {
        val type = extractChangeType(lines)
        val (oldFileName, newFileName) = extractFileNames(lines, type)
        LOG.info("Parsing $type change: $oldFileName -> $newFileName")
        val changeDTO = ChangeDTO(
                type = type,
                oldFileName = oldFileName,
                newFileName = newFileName,
                parentCommitId = parentCommitId,
                isBinary = lines.any { it.startsWith("Binary files") },
                isBlame = isBlameParser)
        addAnnotatedLines(changeDTO)
        addHunks(lines, changeDTO)
        return changeDTO
    }

    protected abstract fun addAnnotatedLines(changeDTO: ChangeDTO)

    protected abstract fun addHunks(lines: List<String>, changeDTO: ChangeDTO)

    protected fun getHunks(lines: List<String>): List<List<String>> {
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
                currentHunkLines.add(it)
            }
            LOG.info("Found ${hunks.size} hunks")
            hunks
        }
    }

    private fun extractChangeType(lines: List<String>): ChangeType {
        var changeTypeLine = lines.find { it.startsWith("new file mode") }
        if (changeTypeLine != null)
            return ChangeType.ADD
        changeTypeLine = lines.find { it.startsWith("deleted file mode") }
        if (changeTypeLine != null)
            return ChangeType.DELETE
        changeTypeLine = lines.find { it.startsWith("similarity index") }
        if (changeTypeLine != null)
            return ChangeType.RENAME
        return ChangeType.MODIFY
    }

    private fun extractFileNames(lines: List<String>, type: ChangeType): Pair<String, String> {
        val diffLine = lines[0]
        return when (type) {
            ChangeType.ADD -> extractFileNames(diffLine, fromName = devNull)
            ChangeType.DELETE -> extractFileNames(diffLine, toName = devNull)
            else -> extractFileNames(diffLine)
        }
    }

    private fun extractFileNames(line: String, fromName: String? = null, toName: String? = null): Pair<String, String> {
        val fromNameIndex = line.indexOf("a/") + 2
        val lastSpaceIndex = line.lastIndexOf(" ")
        val toNameIndex = lastSpaceIndex + 3
        return if (line.contains("--combined")) {
            val fileName = line.substring(lastSpaceIndex).trim()
            Pair(fileName, fileName)
        } else Pair((fromName ?: line.substring(fromNameIndex, lastSpaceIndex)).trim(),
                (toName ?: line.substring(toNameIndex)).trim())
    }
}