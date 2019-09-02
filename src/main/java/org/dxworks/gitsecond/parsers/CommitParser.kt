package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.model.AuthorID

class CommitParser(val lines: MutableList<String>) {
    val isMergeCommit: Boolean = lines.removeIf { it.startsWith("Merge") }
    val commitId: String
    val authorId: AuthorID

    init {
        commitId = extractCommitId()
        authorId = extractAuthorId()
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
}