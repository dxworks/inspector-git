package org.dxworks.inspectorgit.gitclient.iglog.writers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import org.dxworks.inspectorgit.gitclient.utils.maskString

class IGCommitWriter(private val commitDTO: CommitDTO) : IGWriter() {
    private val incognito = System.getenv("GIT_INCOGNITO")?.toBoolean() ?: false

    override fun appendLines(responseBuilder: StringBuilder) {
        responseBuilder.appendln(getIdLine())
        responseBuilder.appendln(getParentsLine())
        responseBuilder.appendln(commitDTO.authorDate)
        responseBuilder.appendln(commitDTO.authorEmail.let { if(incognito) maskString(it) else it})
        responseBuilder.appendln(commitDTO.authorName.let { if(incognito) maskString(it) else it})
        if (commitDTO.authorDate != commitDTO.committerDate) {
            responseBuilder.appendln(commitDTO.committerDate)
            responseBuilder.appendln(commitDTO.committerEmail.let { if(incognito) maskString(it) else it})
            responseBuilder.appendln(commitDTO.committerName.let { if(incognito) maskString(it) else it})
        }
        responseBuilder.appendln(getMessageLine())

        commitDTO.changes.forEach { responseBuilder.append(IGChangeWriter(it).write()) }
    }

    private fun getMessageLine() = "${IGLogConstants.messagePrefix}${getFormattedMessage()}"

    private fun getFormattedMessage() = commitDTO.message.replace("\n", "\n${IGLogConstants.messagePrefix}")

    private fun getIdLine() = "${IGLogConstants.commitIdPrefix}${commitDTO.id}"

    private fun getParentsLine() = commitDTO.parentIds.joinToString(" ")

}
