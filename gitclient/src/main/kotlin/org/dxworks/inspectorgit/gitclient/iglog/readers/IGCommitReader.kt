package org.dxworks.inspectorgit.gitclient.iglog.readers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants

class IGCommitReader(private val igChangeReader: IGChangeReader = IGChangeReader()) {


    fun read(lines: MutableList<String>): CommitDTO {
        val id = lines.removeAt(0).removePrefix(IGLogConstants.commitIdPrefix)
        val parentIds = lines.removeAt(0).split(" ")
        val authorDate = lines.removeAt(0)
        val authorEmail = lines.removeAt(0)
        val authorName = lines.removeAt(0)
        var committerDate = ""
        var committerEmail = ""
        var committerName = ""
        val message = if (lines.first().startsWith(IGLogConstants.messagePrefix)) {
            extractMessage(lines)
        } else {
            committerDate = lines.removeAt(0)
            committerEmail = lines.removeAt(0)
            committerName = lines.removeAt(0)
            extractMessage(lines)
        }

        var currentChangeLines: MutableList<String> = ArrayList()
        val changes: MutableList<ChangeDTO> = ArrayList()
        lines.forEach {
            if (it.startsWith(IGLogConstants.changePrefix)) {
                if (currentChangeLines.isNotEmpty()) changes.add(igChangeReader.read(currentChangeLines))
                currentChangeLines = ArrayList()
            }
            currentChangeLines.add(it)
        }
        if (currentChangeLines.isNotEmpty()) changes.add(igChangeReader.read(currentChangeLines))
        return CommitDTO(id,
                parentIds,
                authorName,
                authorEmail,
                authorDate,
                committerName,
                committerEmail,
                committerDate,
                message,
                changes)

    }

    private fun extractMessage(commitLines: MutableList<String>): String {
        val messageBuilder = StringBuilder()
        while (commitLines.isNotEmpty() && commitLines.first().startsWith(IGLogConstants.messagePrefix)) {
            messageBuilder.appendLine(commitLines.removeAt(0).removePrefix(IGLogConstants.messagePrefix))
        }
        return messageBuilder.toString().trim()
    }

}
