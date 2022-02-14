package org.dxworks.inspectorgit.gitclient.iglog.writers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import org.dxworks.inspectorgit.gitclient.incognito.CharTransformer
import org.dxworks.inspectorgit.gitclient.incognito.encryptString

class IGCommitWriter(private val commitDTO: CommitDTO, incognito: Boolean) : IGWriter(incognito) {

    override fun appendLines(responseBuilder: StringBuilder) {
        responseBuilder.appendln(getIdLine())
        responseBuilder.appendln(getParentsLine())
        responseBuilder.appendln(commitDTO.authorDate)
        responseBuilder.appendln(commitDTO.authorEmail.let { if (incognito) encryptString(it) else it })
        responseBuilder.appendln(commitDTO.authorName.let { if (incognito) encryptString(it) else it })
        if (commitDTO.authorDate != commitDTO.committerDate) {
            responseBuilder.appendln(commitDTO.committerDate)
            responseBuilder.appendln(commitDTO.committerEmail.let { if (incognito) encryptString(it) else it })
            responseBuilder.appendln(commitDTO.committerName.let { if (incognito) encryptString(it) else it })
        }
        responseBuilder.appendln(getMessageLine())

        commitDTO.changes.forEach { responseBuilder.append(IGChangeWriter(it).write()) }
    }

    private fun getMessageLine() = "${IGLogConstants.messagePrefix}${getFormattedMessage()}"

    private fun getFormattedMessage() = commitDTO.message.replace("\n", "\n${IGLogConstants.messagePrefix}")

    private fun getIdLine() = "${IGLogConstants.commitIdPrefix}${commitDTO.id}"

    private fun getParentsLine() = commitDTO.parentIds.joinToString(" ")

}
