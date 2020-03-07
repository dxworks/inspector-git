package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.slf4j.LoggerFactory

data class File(val isBinary: Boolean, val changes: MutableList<Change> = ArrayList()) {

    companion object {
        private val LOG = LoggerFactory.getLogger(File::class.java)
    }

    fun isAlive(commit: Commit?): Boolean {
        val type = getLastChange(commit)?.type
        return type != null && type != ChangeType.DELETE
    }

    fun annotatedLines(commit: Commit?): List<AnnotatedLine> {
        return getLastChange(commit)?.annotatedLines ?: emptyList()
    }

    fun getLastChange(commit: Commit?): Change? {
        return when {
            changes.isEmpty() -> null
            commit == null -> changes.last()
            else -> {
                getLastChangeRecursively(commit)
            }
        }
    }

    private tailrec fun getLastChangeRecursively(commit: Commit): Change? {
        val change = changes.find { it.commit == commit }
        return if (change != null) change else {
            val parent = commit.parents.firstOrNull()
            if (parent == null) null else getLastChangeRecursively(parent)
        }
    }
}
