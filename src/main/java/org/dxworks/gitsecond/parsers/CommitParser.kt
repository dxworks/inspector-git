package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.model.AuthorID
import java.util.*
import kotlin.collections.ArrayList

class CommitParser(private val lines: MutableList<String>) {
    val isMergeCommit: Boolean = lines.removeIf { it.startsWith("Merge") }
    val commitId: String
    val authorId: AuthorID
    val date: Date
    val message: String
    val changeParsers: List<ChangeParser>

    init {
        commitId = extractCommitId()
        authorId = extractAuthorId()
        date = extractDate()
        message = extractMessage()
        changeParsers = createChangeParsers()
    }

    private fun extractCommitId(): String {
        val commitLine = lines.removeAt(0)
        return commitLine.split(" ")[1]
    }

    private fun extractAuthorId(): AuthorID {
        val authorLine = lines.removeAt(0).removePrefix("Author: ")
        val authorDetails = authorLine.split(" <")
        return AuthorID(name = authorDetails[0], email = authorDetails[1].removeSuffix(">"))
    }

    private fun extractDate(): Date {
        val dateLine = lines.removeAt(0)
        return Date(dateLine.removePrefix("Date: "))
    }

    private fun extractMessage(): String {
        var message: String = ""
        while (lines.isNotEmpty() && !lines[0].startsWith("diff ")) {
            message += lines.removeAt(0).trim()
        }
        return message
    }

    private fun createChangeParsers(): List<ChangeParser> {
        return if (lines.isNotEmpty()) {
            val changes: MutableList<MutableList<String>> = ArrayList()
            var currentChangeLines: MutableList<String> = ArrayList()
            lines.forEach() {
                if (it.startsWith("diff ")) {
                    currentChangeLines = ArrayList()
                    changes.add(currentChangeLines)
                }
                currentChangeLines.add(it)
            }
            changes.map { ChangeParser(it) }
        } else ArrayList()
    }
}
