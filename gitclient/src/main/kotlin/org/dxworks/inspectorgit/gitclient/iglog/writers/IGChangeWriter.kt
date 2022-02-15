package org.dxworks.inspectorgit.gitclient.iglog.writers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants

class IGChangeWriter(private val changeDTO: ChangeDTO) : IGWriter() {
    override fun appendLines(responseBuilder: StringBuilder) {
        responseBuilder.appendLine(getTypeLine())
        responseBuilder.appendLine(changeDTO.parentCommitId)
        responseBuilder.appendLine(getFileNames())

        changeDTO.hunks.forEach { responseBuilder.append(IGHunkWriter(it).write()) }
    }

    private fun getFileNames() = when (changeDTO.type) {
        ChangeType.ADD -> changeDTO.newFileName
        ChangeType.DELETE -> changeDTO.oldFileName
        ChangeType.RENAME -> "${changeDTO.oldFileName}\n${changeDTO.newFileName}"
        ChangeType.MODIFY -> changeDTO.newFileName
    }

    private fun getTypeLine() = "${IGLogConstants.changePrefix}${getTypeLetter()}${isBinary()}"

    private fun isBinary(): String = if (changeDTO.isBinary) "b" else ""

    private fun getTypeLetter() = when (changeDTO.type) {
        ChangeType.ADD -> "A"
        ChangeType.DELETE -> "D"
        ChangeType.RENAME -> "R"
        ChangeType.MODIFY -> "M"
    }

}
