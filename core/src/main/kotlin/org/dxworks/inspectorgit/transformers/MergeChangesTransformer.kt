package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

class MergeChangesTransformer(private val changeDTOs: List<ChangeDTO>, val commit: Commit, val project: Project, private val changeFactory: ChangeFactory) {

    companion object {
        val LOG = LoggerFactory.getLogger(MergeChangesTransformer::class.java)
    }

    fun transform(): List<Change> {
        val changes = changeDTOs.mapNotNull { ChangeTransformer(it, commit, project, changeFactory).transform() }
        return if (changes.isEmpty()) return emptyList() else mergeChanges(changes)
    }

    private fun mergeChanges(changes: List<Change>): List<Change> {
        LOG.info("Merging ${changes.size} changes")
        val firstChange = changes.first()
        if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE }) {
            val cleanParent = commit.parents.first { changes.none { change -> change.parentCommit == it } }
            firstChange.file.getLastChange(cleanParent)?.let { lastChange ->
                if (lastChange.annotatedLines.size == firstChange.annotatedLines.size)
                firstChange.annotatedLines = lastChange.annotatedLines.map { AnnotatedLine(it.number, it.content) }
            }
        } else {
            val annotatedFiles = changes.map { it.annotatedLines }
            val fileSize = annotatedFiles.first().size
            for (i in 0 until fileSize) {
                val currentAnnotatedLines = annotatedFiles.map { it[i] }
                val firstAnnotatedLine = currentAnnotatedLines[0]
                val annotatedLines = currentAnnotatedLines.drop(1)
                if (firstAnnotatedLine.content.commit == commit)
                    annotatedLines.find { it.content.commit != commit }?.let { firstAnnotatedLine.content = it.content }
            }
        }

        changes.drop(1).forEach { it.annotatedLines = changes.first().annotatedLines }

        val files = changes.map { it.file }.distinct()
        if (files.size > 1) {
            val allFileChanges = files.flatMap { it.changes }.distinct()
            val file = files.first()
            file.changes = allFileChanges.sortedBy { it.commit.committerDate }.toMutableList()
            changes.forEach { it.file = file }

            files.drop(1).forEach { project.fileRegistry.delete(it) }
        }

        LOG.info("Finished merging changes")
        return changes
    }
}
