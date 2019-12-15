package org.dxworks.inspectorgit.gitClient.parsers.abstracts

import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.dto.CommitDTO
import org.dxworks.inspectorgit.gitClient.parsers.GitParser
import org.slf4j.LoggerFactory
import java.util.*

abstract class CommitParser : GitParser<CommitDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(CommitParser::class.java)
    }

    private val author = "author"
    private val committer = "committer"

    override fun parse(lines: List<String>): CommitDTO {
        val mutableLines = lines.toMutableList()
        val commitId = extractCommitId(mutableLines)
        LOG.info("Parsing commit with id: $commitId")
        val parentIds = extractParentIds(mutableLines)
        return CommitDTO(
                id = commitId,
                parentIds = parentIds,
                authorName = extractName(mutableLines, author),
                authorEmail = extractEmail(mutableLines, author),
                authorDate = extractDate(mutableLines, author),
                committerName = extractName(mutableLines, committer),
                committerEmail = extractEmail(mutableLines, committer),
                committerDate = extractDate(mutableLines, committer),
                message = extractMessage(mutableLines),
                changes = getChanges(mutableLines, commitId, parentIds))
    }

    abstract fun getChanges(lines: List<String>, commitId: String, parentIds: List<String>): List<ChangeDTO>

    protected fun extractChanges(lines: List<String>): List<List<String>> {
        val changes: MutableList<MutableList<String>> = ArrayList()
        var currentChangeLines: MutableList<String> = ArrayList()
        LOG.info("Extracting changes")
        lines.forEach {
            if (it.startsWith("diff ")) {
                currentChangeLines = ArrayList()
                changes.add(currentChangeLines)
            }
            currentChangeLines.add(it)
        }
        LOG.info("Found ${changes.size} changes")
        return changes
    }

    private fun extractCommitId(lines: MutableList<String>) =
            lines.removeAt(0).removePrefix("commit: ")

    private fun extractParentIds(lines: MutableList<String>) =
            lines.removeAt(0).removePrefix("parents: ").split(" ")

    private fun extractName(lines: MutableList<String>, devType: String) =
            lines.removeAt(0).removePrefix("$devType name: ")

    private fun extractEmail(lines: MutableList<String>, devType: String) =
            lines.removeAt(0).removePrefix("$devType email: ")


    private fun extractDate(lines: MutableList<String>, devType: String): String =
            lines.removeAt(0).removePrefix("$devType date: ").trim()

    private fun extractMessage(lines: MutableList<String>): String {
        lines.removeAt(0)
        var message = ""
        while (lines.isNotEmpty() && !lines[0].startsWith("diff ")) {
            message = "$message\n${lines.removeAt(0)}"
        }
        return message.trim()
    }
}