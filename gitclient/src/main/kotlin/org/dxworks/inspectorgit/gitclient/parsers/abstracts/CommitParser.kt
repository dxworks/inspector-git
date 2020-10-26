package org.dxworks.inspectorgit.gitclient.parsers.abstracts

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants.Companion.gitLogDiffLineStart
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants.Companion.gitLogMessageEnd
import org.dxworks.inspectorgit.gitclient.parsers.GitParser
import org.slf4j.LoggerFactory
import java.util.*

abstract class CommitParser : GitParser<CommitDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(CommitParser::class.java)
    }

    override fun parse(lines: List<String>): CommitDTO {
        val mutableLines = lines.toMutableList()
        val commitId = extractCommitId(mutableLines)
        LOG.debug("Parsing commit with id: $commitId")
        val parentIds = extractParentIds(mutableLines)
        return CommitDTO(
                id = commitId,
                parentIds = parentIds,
                authorName = mutableLines.removeAt(0).trim(),
                authorEmail = mutableLines.removeAt(0).trim(),
                authorDate = mutableLines.removeAt(0).trim(),
                committerName = mutableLines.removeAt(0).trim(),
                committerEmail = mutableLines.removeAt(0).trim(),
                committerDate = mutableLines.removeAt(0).trim(),
                message = extractMessage(mutableLines),
                changes = getChanges(mutableLines, commitId, parentIds))
    }

    abstract fun getChanges(lines: List<String>, commitId: String, parentIds: List<String>): List<ChangeDTO>

    protected fun extractChanges(lines: List<String>): List<List<String>> {
        val changes: MutableList<MutableList<String>> = ArrayList()
        var currentChangeLines: MutableList<String> = ArrayList()
        LOG.debug("Extracting changes")
        lines
                .dropWhile { !it.startsWith(gitLogDiffLineStart) }
                .forEach {
                    if (it.startsWith(gitLogDiffLineStart)) {
                        currentChangeLines = ArrayList()
                        changes.add(currentChangeLines)
                    }
                    currentChangeLines.add(it)
                }
        LOG.debug("Found ${changes.size} changes")
        return changes
    }

    private fun extractCommitId(lines: MutableList<String>) =
            lines.removeAt(0).removePrefix(IGLogConstants.commitIdPrefix)

    private fun extractParentIds(lines: MutableList<String>) =
            lines.removeAt(0).split(" ").filter { it.isNotEmpty() }


    private fun extractMessage(lines: MutableList<String>): String {
        var message = ""
        while (lines.isNotEmpty() && lines[0] != gitLogMessageEnd) {
            message = "$message\n${lines.removeAt(0)}"
        }
        lines.removeAt(0)
        return message.trim()
    }
}
