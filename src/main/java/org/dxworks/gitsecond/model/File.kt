package org.dxworks.gitsecond.model

import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

data class File(var fullyQualifiedName: String, var changes: MutableList<Change>, var aliases: MutableList<String> = Collections.singletonList(fullyQualifiedName), var isAlive: Boolean = true) {
    fun contentForRevision(commit: Commit): String {
        val changeForCommit = changes.first { it.commit == commit }

        return changeForCommit.annotatedLines.joinToString(separator = "\n") { it.content }
    }

    var name = fullyQualifiedName.split("/").last()

    val path: Path
        get() = Paths.get(fullyQualifiedName)
}
