package org.dxworks.inspectorgit.gitclient.iglog

import org.dxworks.inspectorgit.gitclient.dto.CommitDTO

class IGCommitWriter(private val commitDTO: CommitDTO) : IGWriter() {
    override fun appendLines(responseBuilder: StringBuilder) {
        responseBuilder.appendln(getIdLine())
        responseBuilder.appendln(getParentsLine())
        responseBuilder.appendln(commitDTO.authorName)
        responseBuilder.appendln(commitDTO.authorEmail)
        responseBuilder.appendln(commitDTO.authorDate)
        if (commitDTO.authorDate != commitDTO.committerDate) {
            responseBuilder.appendln(commitDTO.committerName)
            responseBuilder.appendln(commitDTO.committerEmail)
            responseBuilder.appendln(commitDTO.committerDate)
        }
        responseBuilder.appendln(getMessageLine())

        commitDTO.changes.forEach { responseBuilder.appendln(IGChangeWriter(it).write()) }
    }

    private fun getMessageLine() = "${IGLogConstants.messagePrefix}${commitDTO.message}"

    private fun getIdLine() = "${IGLogConstants.commitIdPrefix}${commitDTO.id}"

    private fun getParentsLine() = commitDTO.parentIds.joinToString(" ")

}
