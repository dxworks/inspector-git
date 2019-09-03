package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.dto.ChangeDTO
import org.dxworks.gitsecond.dto.CommitDTO
import org.dxworks.gitsecond.model.AuthorID
import java.util.*

abstract class CommitParser(protected val lines: MutableList<String>) {
    private var parsed: Boolean = false
    protected val commitId = extractCommitId()

    lateinit var commit: CommitDTO
        private set

    fun parse(): CommitParser {
        if (!parsed) {
            commit = CommitDTO(
                    commitId = commitId,
                    parentIds = extractParentIds(),
                    authorId = extractAuthorId(),
                    date = extractDate(),
                    message = extractMessage(),
                    changes = extractChanges())
            parsed = true
        }
        return this
    }

    abstract fun extractChanges(): List<ChangeDTO>

    protected fun getChanges(): List<MutableList<String>> {
        val changes: MutableList<MutableList<String>> = ArrayList()
        var currentChangeLines: MutableList<String> = ArrayList()
        lines.forEach {
            if (it.startsWith("diff ")) {
                currentChangeLines = ArrayList()
                changes.add(currentChangeLines)
            }
            currentChangeLines.add(it)
        }
        return changes
    }

    private fun extractCommitId(): String {
        return lines.removeAt(0).removePrefix("commit: ")
    }

    private fun extractParentIds(): List<String> {
        return lines.removeAt(0).removePrefix("parents: ").split(" ")
    }

    private fun extractAuthorId(): AuthorID {
        val authorName = lines.removeAt(0).removePrefix("author name: ")
        val authorEmail = lines.removeAt(0).removePrefix("author email: ")
        return AuthorID(authorEmail, authorName)
    }

    private fun extractDate(): Date {
        return Date(lines.removeAt(0).removePrefix("date: "))
    }

    private fun extractMessage(): String {
        lines.removeAt(0)
        var message: String = ""
        while (lines.isNotEmpty() && !lines[0].startsWith("diff ")) {
            message = "$message\n${lines.removeAt(0)}"
        }
        return message.trim()
    }
}