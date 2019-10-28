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
                .forEach { newAnnotatedLines.add(it.lineNumber - 1, it.annotatedLine) }

        reindex(newAnnotatedLines)
        annotatedLines = newAnnotatedLines
    }

    private fun reindex(annotatedLines: MutableList<AnnotatedLine>) {
        annotatedLines.forEachIndexed { index, annotatedLine -> annotatedLine.number = index + 1 }
    }
}