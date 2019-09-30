package org.dxworks.inspectorgit.parsers.abstracts

import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.CommitDTO
import org.dxworks.inspectorgit.parsers.GitParser
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Slf4j
abstract class CommitParser : GitParser<CommitDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(CommitParser::class.java)
    }

    private val author = "author"
    private val committer = "committer"
    private val dateFormat = "EEE MMM d HH:mm:ss yyyy Z"

    override fun parse(lines: MutableList<String>): CommitDTO {
        val commitId = extractCommitId(lines)
        LOG.info("Parsing commit with id: $commitId")
        val parentIds = extractParentIds(lines)
        return CommitDTO(
                id = commitId,
                parentIds = parentIds,
                authorName = extractName(lines, author),
                authorEmail = extractEmail(lines, author),
                authorDate = extractDate(lines, author),
                committerName = extractName(lines, committer),
                committerEmail = extractEmail(lines, committer),
                committerDate = extractDate(lines, committer),
                message = extractMessage(lines),
                changes = extractChanges(lines, commitId, parentIds))
    }

    abstract fun extractChanges(lines: MutableList<String>, commitId: String, parentIds: List<String>): List<ChangeDTO>

    protected fun getChanges(lines: List<String>): List<MutableList<String>> {
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

    private fun extractCommitId(lines: MutableList<String>): String {
        return lines.removeAt(0).removePrefix("commit: ")
    }

    private fun extractParentIds(lines: MutableList<String>): List<String> {
        return lines.removeAt(0).removePrefix("parents: ").split(" ")
    }

    private fun extractName(lines: MutableList<String>, devType: String): String {
        return lines.removeAt(0).removePrefix("$devType name: ")
    }

    private fun extractEmail(lines: MutableList<String>, devType: String): String {
        return lines.removeAt(0).removePrefix("$devType email: ")
    }


    private fun extractDate(lines: MutableList<String>, devType: String): Date {
        val timeStamp = lines.removeAt(0).removePrefix("$devType date: ").trim()
        return SimpleDateFormat(dateFormat).parse(timeStamp)
    }

    private fun extractMessage(lines: MutableList<String>): String {
        lines.removeAt(0)
        var message: String = ""
        while (lines.isNotEmpty() && !lines[0].startsWith("diff ")) {
            message = "$message\n${lines.removeAt(0)}"
        }
        return message.trim()
    }
}