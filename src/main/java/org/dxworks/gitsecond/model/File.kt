package org.dxworks.gitsecond.model

import java.nio.file.Path
import java.nio.file.Paths

data class File(var fullyQualifiedName: String, var isAlive: Boolean = true) {
    val changes: MutableMap<String, Change> = HashMap()
    val aliases: MutableMap<String, String> = HashMap()

    fun annotatedLinesForRevision(commit: Commit): List<AnnotatedLine>? {
        val change = changes[commit.id]
        return change?.annotatedLines ?: getLastChange(commit.parents).annotatedLines
    }

    private fun getLastChange(commits: List<Commit>): Change {
        return commits.mapNotNull { changes[it.id] }
                .firstOrNull() ?: getLastChange(commits.flatMap { it.parents })
    }

    fun addChange(change: Change) {
        changes[change.commit.id] = change
        aliases[change.commit.id] = change.newFileName
    }


    var name = fullyQualifiedName.split("/").last()

    val path: Path
        get() = Paths.get(fullyQualifiedName)
}
