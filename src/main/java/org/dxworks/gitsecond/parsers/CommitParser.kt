package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.model.AuthorID
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class CommitParser(private val lines: MutableList<String>) {
    val isMergeCommit: Boolean
    val commitId: String
    val parentIds: List<String>
    val authorId: AuthorID
    val date: Date
    val message: String
    val changeParsers: List<ChangeParser>
    val filesContentsAtMergeCommit: Map<String, String>

    init {
        commitId = extractCommitId()
        parentIds = extractParentIds()
        isMergeCommit = parentIds.size > 1
        authorId = extractAuthorId()
        date = extractDate()
        message = extractMessage()
        changeParsers = if (isMergeCommit) ArrayList() else createChangeParsers()
        filesContentsAtMergeCommit = if (isMergeCommit) getFilesContents() else HashMap()
    }

    private fun getFilesContents(): Map<String, String> {
        val changedFileNames = getChangedFileNames()
        val filesMap: MutableMap<String, String> = HashMap()



        return filesMap
    }

    private fun getChangedFileNames(): Set<String> {
        val changedFileNames: MutableSet<String> = HashSet()

        return changedFileNames
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
