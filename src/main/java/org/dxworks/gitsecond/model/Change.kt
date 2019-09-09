package org.dxworks.gitsecond.model


data class Change(var commit: Commit, var type: ChangeType, var file: File, var oldFilename: String, var newFileName: String, var lineChanges: List<LineChange>, var annotatedLines: List<AnnotatedLine>) {
    var parent: Change? = if (type == ChangeType.ADD || file.changes.isEmpty()) null else getParentChange(commit.parents)
    val isRenameChange: Boolean
        get() = type == ChangeType.RENAME

    private fun getParentChange(commits: List<Commit>): Change? {
        return if (commits.isEmpty()) null
        else commits.flatMap { it.changes }.find { it.file == file }
                ?: getParentChange(commits.flatMap { it.parents })
    }

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
            newAnnotatedLines.add(AnnotatedLine(commit, it.lineNumber, it.content))
        }

        reindex(newAnnotatedLines)

        annotatedLines = newAnnotatedLines
    }


    private fun reindex(annotatedLines: MutableList<AnnotatedLine>) {
        annotatedLines.forEachIndexed { index, annotatedLine -> annotatedLine.number = index + 1 }
    }

}