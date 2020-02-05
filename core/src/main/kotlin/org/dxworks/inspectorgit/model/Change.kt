package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.gitClient.enums.LineOperation
import org.slf4j.LoggerFactory

open class Change(val commit: Commit,
                  val type: ChangeType,
                  val file: File,
                  var parentCommits: List<Commit>,
                  var lineChanges: List<LineChange>,
                  var annotatedLines: List<AnnotatedLine> = emptyList(),
                  protected var parentChange: Change?) {

    val parents: List<Change> by lazy { parentCommits.mapNotNull { file.getLastChange(it) } }

    companion object {
        private val LOG = LoggerFactory.getLogger(Change::class.java)
    }

    init {
        LOG.info("Applying ${lineChanges.size} line changes for ${file.id} having ${parentChange?.annotatedLines?.size
                ?: 0} lines")
        applyLineChanges(parentChange)
    }

    private fun applyLineChanges(parentChange: Change?) {
        val newAnnotatedLines = parentChange?.annotatedLines
                ?.map { AnnotatedLine(it.commit, it.number, it.content) }?.toMutableList() ?: ArrayList()
        newAnnotatedLines.removeAll(lineChanges.filter { it.operation == LineOperation.DELETE }.map { it.annotatedLine })

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
        if (file != other.file) return false
        if (lineChanges != other.lineChanges) return false
        if (annotatedLines != other.annotatedLines) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + file.hashCode()
        result = 31 * result + lineChanges.hashCode()
        result = 31 * result + annotatedLines.hashCode()
        return result
    }
}