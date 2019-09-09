package org.dxworks.gitinspector.model

import org.dxworks.gitinspector.model.AnnotatedLine
import java.nio.file.Path
import java.nio.file.Paths

data class File(var fullyQualifiedName: String, var isAlive: Boolean = true) {
    val changes: MutableMap<String, Change> = HashMap()
    val aliases: MutableMap<String, String> = HashMap()

    fun annotatedLinesForRevision(commit: Commit): List<AnnotatedLine> {
        val change = changes[commit.id]
        return change?.annotatedLines ?: getLastChangeAnnotatedLines(commit.parents)
    }

    private fun getLastChangeAnnotatedLines(commits: List<Commit>): List<AnnotatedLine> {
        return if (commits.isEmpty())
            return emptyList()
        else
            commits.mapNotNull { changes[it.id] }
                    .firstOrNull()?.annotatedLines ?: getLastChangeAnnotatedLines(commits.flatMap { it.parents })
    }

    fun addChange(change: Change) {
        changes[change.commit.id] = change
        aliases[change.commit.id] = change.newFileName
    }


    var name = fullyQualifiedName.split("/").last()

    val path: Path
        get() = Paths.get(fullyQualifiedName)
}
