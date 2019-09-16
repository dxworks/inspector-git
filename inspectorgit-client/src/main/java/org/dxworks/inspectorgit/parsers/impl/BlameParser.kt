package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.GitClient
import org.dxworks.inspectorgit.dto.AnnotatedLineDTO
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.HunkDTO
import org.dxworks.inspectorgit.parsers.abstracts.ChangeParser
import org.dxworks.inspectorgit.utils.devNull

class BlameParser(private val gitClient: GitClient, private val commitId: String, otherCommitId: String) : ChangeParser(otherCommitId) {
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