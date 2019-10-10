package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.GitClient
import org.dxworks.inspectorgit.dto.AnnotatedLineDTO
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.HunkDTO
import org.dxworks.inspectorgit.dto.LineChangeDTO
import org.dxworks.inspectorgit.enums.LineOperation
import org.dxworks.inspectorgit.parsers.abstracts.ChangeParser
import org.dxworks.inspectorgit.utils.devNull
import java.util.*

class BlameParser(private val gitClient: GitClient, private val commitId: String, parentCommitId: String) : ChangeParser(parentCommitId) {
    override val isBlameParser: Boolean
        get() = true

    override fun addHunks(lines: List<String>, changeDTO: ChangeDTO) {
        val newAnnotatedLines = changeDTO.annotatedLines.filter { it.commitId == commitId }
        if (newAnnotatedLines.isNotEmpty()) {
            changeDTO.hunks = Collections.singletonList(HunkDTO(newAnnotatedLines
                    .map { LineChangeDTO(LineOperation.ADD, it.number, it.content) }))
            changeDTO.isBlame = false
        }
    }

    override fun addAnnotatedLines(changeDTO: ChangeDTO) {
        if (changeDTO.newFileName != devNull && !changeDTO.isBinary) {
            changeDTO.annotatedLines = gitClient.blame(commitId, changeDTO.newFileName)
                    .filter { it.isNotBlank() }.map { parseAnnotatedLine(it) }
        }
    }

    private fun parseAnnotatedLine(it: String): AnnotatedLineDTO {
        val commitDelimiterIndex = it.indexOf(" ")
        val commitId = it.substring(0, commitDelimiterIndex)
        val other = it.substring(commitDelimiterIndex + 2)
        val contentDelimiterIndex = other.indexOf(")")
        val authorTimeLineNo = other.substring(0, contentDelimiterIndex)
        val content = other.substring(contentDelimiterIndex + 2)
        val lineNumber = authorTimeLineNo.substring(authorTimeLineNo.lastIndexOf(" ") + 1).toInt()
        return AnnotatedLineDTO(commitId, lineNumber, content)
    }
}