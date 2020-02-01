package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.Paths

data class File(val isBinary: Boolean, val changes: MutableList<Change> = ArrayList()) {

    companion object {
        private val LOG = LoggerFactory.getLogger(File::class.java)
    }

    val name get() = name(null)

    val path get() = path(null)

    val fullyQualifiedName get() = fullyQualifiedName(null)

    val lastChange get() = searchLastChange(null)

    val isAlive get() = isAlive(null)

    val annotatedLines get() = annotatedLines(null)

    fun fullyQualifiedName(commit: Commit?): String? = searchLastChange(commit)?.newFileName

    fun name(commit: Commit?): String? = fullyQualifiedName(commit)?.split("/")?.last()

    fun path(commit: Commit?): Path? = fullyQualifiedName(commit)?.let { Paths.get(it) }

    fun isAlive(commit: Commit?): Boolean {
        val type = searchLastChange(commit)?.type
        return type != null && type != ChangeType.DELETE
    }

    fun annotatedLines(commit: Commit?): List<AnnotatedLine> {
        return searchLastChange(commit)?.annotatedLines ?: emptyList()
    }

    fun getLastChange(commit: Commit) = searchLastChange(commit)

    private fun searchLastChange(commit: Commit?): Change? {
        return when {
            commit == null -> changes.last()
            changes.isEmpty() -> null
            else -> {
                val splitCommitIds: MutableSet<String> = HashSet()
                val potentialLastChanges = searchLastChangeRecursively(commit, splitCommitIds)
                val maxChangeByListIndex = potentialLastChanges.maxBy { changes.indexOf(it) }
                val maxChangeByCommitterTimestamp = potentialLastChanges.maxBy { it.commit.committerDate }
                if (maxChangeByListIndex != maxChangeByCommitterTimestamp)
                    LOG.error("last changes by timestamp and by index are different")
                maxChangeByCommitterTimestamp
            }
        }
    }

    private fun searchLastChangeRecursively(commit: Commit, splitCommitIds: MutableSet<String>): List<Change> {
        return if (splitCommitIds.contains(commit.id))
            emptyList()
        else {
            if (commit.isSplitCommit)
                splitCommitIds.add(commit.id)
            changes.find { it.commit == commit }?.let { listOf(it) }
                    ?: commit.parents.flatMap { searchLastChangeRecursively(it, splitCommitIds) }
        }
    }
}
