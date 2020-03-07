package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.slf4j.LoggerFactory

data class File(val isBinary: Boolean, var changes: MutableList<Change> = ArrayList()) {

    val id: Int

    companion object {
        var idCounter = 0
        private val LOG = LoggerFactory.getLogger(File::class.java)
    }

    init {
        idCounter++
        id = idCounter
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as File

        if (isBinary != other.isBinary) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isBinary.hashCode()
        result += id
        return result
    }
}
