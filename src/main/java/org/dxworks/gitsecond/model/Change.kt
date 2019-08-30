package org.dxworks.gitsecond.model


data class Change(var commit: Commit, var type: ChangeType, var file: File, var oldFilename: String, var newFileName: String, var lineChanges: MutableList<LineChange>, var annotatedLines: List<AnnotatedLine>) {
    var parent: Change? = if (file.changes.isEmpty()) null else file.changes.last()

    init {
        if (!commit.isMergeCommit) {
            apply()
        }
    }

    private fun apply() {
        val newAnnotatedLines = if (parent != null) ArrayList(parent!!.annotatedLines) else ArrayList()
        lineChanges.filter { it.operation == LineOperation.REMOVE }
                .forEach { removeChange -> newAnnotatedLines.removeIf { it.lineNumber == removeChange.lineNumber && it.content == removeChange.content } }

        lineChanges.filter { it.operation == LineOperation.ADD }.forEach {
            val annotatedLine = AnnotatedLine(commit, it.lineNumber, it.content)
            if (it.lineNumber > newAnnotatedLines.size) {
                newAnnotatedLines.add(annotatedLine)
            } else {
                newAnnotatedLines.add(it.lineNumber - 1, annotatedLine)
            }
        }

        reindex(newAnnotatedLines)

        annotatedLines = newAnnotatedLines
    }


    private fun reindex(annotatedLines: MutableList<AnnotatedLine>) {
        annotatedLines.forEachIndexed { index, annotatedLine -> annotatedLine.lineNumber = index + 1 }
    }

    fun isRenameChange(): Boolean {
        return type == ChangeType.RENAME
    }
}
