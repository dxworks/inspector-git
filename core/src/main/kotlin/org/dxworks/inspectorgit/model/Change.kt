package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.enums.LineOperation

data class Change(val commit: Commit,
                  val type: ChangeType,
                  val file: File,
                  val parentCommit: Commit?,
                  val oldFileName: String,
                  val newFileName: String,
                  val lineChanges: List<LineChange>,
                  var annotatedLines: List<AnnotatedLine>) {
    val parent: Change? = file.getLastChange(commit)

    val isRenameChange: Boolean
        get() = type == ChangeType.RENAME

    init {
        if (!commit.isMergeCommit) {
            apply()
        }
    }

    private fun apply() {
        val newAnnotatedLines = parent?.annotatedLines
                ?.map { AnnotatedLine(it.commit, it.number, it.content) }?.toMutableList() ?: ArrayList()
        newAnnotatedLines.removeAll(lineChanges.filter { it.operation == LineOperation.REMOVE }.map { it.annotatedLine })

        lineChanges.filter { it.operation == LineOperation.ADD }
                .forEach { newAnnotatedLines.add(it.annotatedLine.number - 1, it.annotatedLine) }

        reindex(newAnnotatedLines)
        annotatedLines = newAnnotatedLines
    }

    private fun reindex(annotatedLines: MutableList<AnnotatedLine>) {
        annotatedLines.forEachIndexed { index, annotatedLine -> annotatedLine.number = index + 1 }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Change

        if (type != other.type) return false
        if (oldFileName != other.oldFileName) return false
        if (newFileName != other.newFileName) return false
        if (lineChanges != other.lineChanges) return false
        if (annotatedLines != other.annotatedLines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + oldFileName.hashCode()
        result = 31 * result + newFileName.hashCode()
        result = 31 * result + lineChanges.hashCode()
        result = 31 * result + annotatedLines.hashCode()
        return result
    }
}