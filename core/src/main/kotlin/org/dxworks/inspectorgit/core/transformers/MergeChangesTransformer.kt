package org.dxworks.inspectorgit.core.transformers

import org.dxworks.inspectorgit.core.ChangeFactory
import org.dxworks.inspectorgit.core.model.Change
import org.dxworks.inspectorgit.core.model.Commit
import org.dxworks.inspectorgit.core.model.Project
import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.slf4j.LoggerFactory

class MergeChangesTransformer(private val changeDTOs: List<ChangeDTO>, val commit: Commit, val project: Project, private val changeFactory: ChangeFactory) {

    companion object {
        val LOG = LoggerFactory.getLogger(MergeChangesTransformer::class.java)
    }

    fun transform(): List<Change> {
        val changes = changeDTOs.mapNotNull { ChangeTransformer(it, commit, project, changeFactory).transform() }
        return if (changes.isEmpty()) return emptyList() else fixChanges(changes)
    }

    private fun fixChanges(changes: List<Change>): List<Change> {
        LOG.info("Merging ${changes.size} changes")

        val missingChange = if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE })
            getMissingChange(changes) else null
        fixAnnotatedLinesCommits(changes, missingChange)
        mergeFiles(changes, missingChange)

        LOG.info("Finished merging changes")
        return changes
    }

    private fun getMissingChange(changes: List<Change>): Change {
        val cleanParent = commit.parents.first { changes.none { change -> change.parentCommit == it } }
        return ChangeTransformer.getLastChange(cleanParent, changes.first().newFileName)
    }

    private fun mergeFiles(changes: List<Change>, missingChange: Change?) {
        val files = (changes.map { it.file } + listOf(missingChange?.file)).filterNotNull().distinct()
        if (files.size > 1) {
            val allFileChanges = files.flatMap { it.changes }.distinct()
            val file = files.first()
            file.changes = allFileChanges.sortedBy { it.commit.committerDate }.toMutableList()
            changes.forEach { it.file = file }

            files.drop(1).forEach { project.fileRegistry.delete(it) }
        }
    }

    private fun fixAnnotatedLinesCommits(changes: List<Change>, missingChange: Change?) {
        missingChange?.let { changes.first().annotatedLines = it.annotatedLines }

        val annotatedFiles = changes.map { it.annotatedLines }
        for (i in annotatedFiles.first().indices) {
            val currentAnnotatedLines = annotatedFiles.map { it[i] }
            val firstAnnotatedLine = currentAnnotatedLines[0]
            val annotatedLines = currentAnnotatedLines.drop(1)
            if (firstAnnotatedLine.commit == commit)
                annotatedLines.find { it.commit != commit }?.let { firstAnnotatedLine.commit = it.commit }
        }
        changes.drop(1).forEach { it.annotatedLines = changes.first().annotatedLines }
    }
}
