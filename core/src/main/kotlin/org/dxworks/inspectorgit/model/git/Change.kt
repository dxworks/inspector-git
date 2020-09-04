package org.dxworks.inspectorgit.model.git

import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.slf4j.LoggerFactory

open class Change(val commit: Commit,
                  val type: ChangeType,
                  val oldFileName: String,
                  val newFileName: String,
                  var file: File,
                  var parentCommit: Commit?,
                  var hunks: List<Hunk>,
                  var annotatedLines: MutableList<Commit> = ArrayList(),
                  protected var parentChange: Change?) {

    val id: String get() = "${commit.id}-$oldFileName->$newFileName"

    val lineChanges: List<LineChange>
        get() = hunks.flatMap { it.lineChanges }

    val deletedLines: List<LineChange>
        get() = hunks.flatMap { it.deletedLines }

    val addedLines: List<LineChange>
        get() = hunks.flatMap { it.addedLines }

    companion object {
        private val LOG = LoggerFactory.getLogger(Change::class.java)
    }

    init {
        LOG.info("Applying ${lineChanges.size} line changes for $newFileName having ${parentChange?.annotatedLines?.size
                ?: 0} lines")
        applyLineChanges(parentChange)
    }

    private fun applyLineChanges(parentChange: Change?) {
        val newAnnotatedLines = parentChange?.annotatedLines
                ?.toMutableList() ?: ArrayList()
        val deletes = deletedLines
        val adds = addedLines
        deletes.sortedByDescending { it.lineNumber }
                .forEach { newAnnotatedLines.removeAt(it.lineNumber - 1) }

        adds.forEach { newAnnotatedLines.add(it.lineNumber - 1, it.commit) }

//        reindex(newAnnotatedLines)
        annotatedLines = newAnnotatedLines
    }

//    private fun reindex(annotatedLines: MutableList<AnnotatedLine>) {
//        annotatedLines.forEachIndexed { index, annotatedLine -> annotatedLine.number = index + 1 }
//    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Change

        if (type != other.type) return false
        if (file != other.file) return false
        if (lineChanges != other.lineChanges) return false
        if (annotatedLines != other.annotatedLines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + commit.hashCode()
        result = 31 * result + oldFileName.hashCode()
        result = 31 * result + newFileName.hashCode()
        result = 31 * result + hunks.hashCode()
        result = 31 * result + annotatedLines.hashCode()
        return result
    }
}