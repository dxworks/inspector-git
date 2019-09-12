package org.dxworks.gitinspector.model

import org.dxworks.gitinspector.enums.ChangeType
import org.dxworks.gitinspector.enums.LineOperation

data class Change(val commit: Commit, val type: ChangeType, val file: File, val otherCommit: Commit, val oldFilename: String, val newFileName: String, var lineChanges: List<LineChange>, var annotatedLines: List<AnnotatedLine>) {
    var parent: Change? = if (type == ChangeType.ADD || file.changes.isEmpty()) null else file.getLastChange(commit)
    val isRenameChange: Boolean
        get() = type == ChangeType.RENAME

    init {
        if (!commit.isMergeCommit) {
            apply()
        }
    }

    private fun apply() {
        val newAnnotatedLines = if (parent != null) ArrayList(parent!!.annotatedLines) else ArrayList()
        lineChanges.filter { it.operation == LineOperation.REMOVE }
                .forEach { removeChange -> newAnnotatedLines.removeIf { it.number == removeChange.lineNumber && it.content == removeChange.content } }

        lineChanges.filter { it.operation == LineOperation.ADD }.forEach {
            val annotatedLine = AnnotatedLine(commit, it.lineNumber, it.content)
            newAnnotatedLines.add(it.lineNumber - 1, annotatedLine)
        }

        reindex(newAnnotatedLines)

        annotatedLines = newAnnotatedLines
    }


    private fun reindex(annotatedLines: MutableList<AnnotatedLine>) {
        annotatedLines.forEachIndexed { index, annotatedLine -> annotatedLine.number = index + 1 }
    }

}