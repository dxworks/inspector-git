package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import java.nio.file.Path
import java.nio.file.Paths

data class File(val isBinary: Boolean, val changes: MutableList<Change> = ArrayList()) {

    val name get() = name(null)

    val path get() = path(null)

    val fullyQualifiedName get() = fullyQualifiedName(null)

    val lastChange get() = getLastChange(null)

    val isAlive get() = isAlive(null)

    val annotatedLines get() = annotatedLines(null)

    fun fullyQualifiedName(commit: Commit?): String? = getLastChange(commit)?.newFileName

    fun name(commit: Commit?): String? = fullyQualifiedName(commit)?.split("/")?.last()

    fun path(commit: Commit?): Path? = fullyQualifiedName(commit)?.let { Paths.get(it) }

    fun isAlive(commit: Commit?): Boolean {
        val type = getLastChange(commit)?.type
        return type != null && type != ChangeType.DELETE
    }

    fun annotatedLines(commit: Commit?): List<AnnotatedLine> {
        return getLastChange(commit)?.annotatedLines ?: emptyList()
    }


    tailrec fun getLastChange(commit: Commit?): Change? {
        return when {
            changes.isEmpty() -> null
            commit == null -> return changes.last()
            else -> {
                val change = changes.find { it.commit == commit }
                if (change != null) change else {
                    val parent = commit.parents.firstOrNull()
                    if (parent == null) null else getLastChange(parent)
                }
            }
        }
    }
}
