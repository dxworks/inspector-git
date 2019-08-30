package org.dxworks.gitsecond.model

import java.nio.file.Path
import java.nio.file.Paths

data class File(var fullyQualifiedName: String, var changes: MutableList<Change>) {
    fun contentForRevision(commit: Commit): String {
        val changeForCommit = changes.first { it.commit == commit }

        return changeForCommit.annotatedLines.joinToString(separator = "\n") { it.content }
    }

    var name = fullyQualifiedName.split("/").last()

    val path: Path
        get() = Paths.get(fullyQualifiedName)
}
