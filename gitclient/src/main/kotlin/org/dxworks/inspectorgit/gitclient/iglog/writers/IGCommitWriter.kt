package org.dxworks.inspectorgit.gitclient.iglog.writers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants

class IGCommitWriter(private val commitDTO: CommitDTO) : IGWriter() {
    override fun appendLines(responseBuilder: StringBuilder) {
        responseBuilder.appendln(getIdLine())
        responseBuilder.appendln(getParentsLine())
        responseBuilder.appendln(commitDTO.authorDate)
        responseBuilder.appendln(commitDTO.authorEmail)
        responseBuilder.appendln(commitDTO.authorName)
        if (commitDTO.authorDate != commitDTO.committerDate) {
            responseBuilder.appendln(commitDTO.committerDate)
            responseBuilder.appendln(commitDTO.committerEmail)
            responseBuilder.appendln(commitDTO.committerName)
        }
        responseBuilder.appendln(getMessageLine())

        commitDTO.changes.forEach { responseBuilder.appendln(IGChangeWriter(it).write()) }
    }

    private fun getMessageLine() = "${IGLogConstants.messagePrefix}${commitDTO.message}"

    private fun getIdLine() = "${IGLogConstants.commitIdPrefix}${commitDTO.id}"

    private fun getParentsLine() = commitDTO.parentIds.joinToString(" ")

}
