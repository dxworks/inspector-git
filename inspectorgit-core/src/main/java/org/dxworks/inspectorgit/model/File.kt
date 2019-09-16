package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.utils.devNull
import java.nio.file.Path
import java.nio.file.Paths

data class File(var fullyQualifiedName: String, val isBinary: Boolean, val changes: MutableList<Change> = ArrayList()) {
    val name: String
        get() = fullyQualifiedName.split("/").last()

    val path: Path
        get() = Paths.get(fullyQualifiedName)

    fun alias(commit: Commit): String {
        return getLastChange(commit)?.newFileName ?: devNull
    }

    fun isAlive(commit: Commit): Boolean {
        val type = getLastChange(commit)?.type
        return type != null && type != ChangeType.DELETE
    }

    fun annotatedLines(commit: Commit): List<AnnotatedLine> {
        return getLastChange(commit)?.annotatedLines ?: emptyList()
    }

    fun getLastChange(commit: Commit): Change? {
        return if (changes.isEmpty() || changes.first().commit.date > commit.date) null
        else changes.find { it.commit == commit } ?: getLastChange(commit.parents.first())
    }
}
