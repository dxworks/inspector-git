package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.gitClient.enums.LineOperation
import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

class MergeChangesTransformer(private val changeDTOs: List<ChangeDTO>, val commit: Commit, val project: Project, private val changeFactory: ChangeFactory) {

    companion object {
        val LOG = LoggerFactory.getLogger(MergeChangesTransformer::class.java)
    }

    fun transform(): Change? {
        val changes = changeDTOs.mapNotNull { ChangeTransformer(it, commit, project, changeFactory).transform() }
        return if (changes.isEmpty()) return null else mergeChanges(changes)
    }

    private fun mergeChanges(changes: List<Change>): Change {
        LOG.info("Merging ${changes.size} changes")
        val firstChange = changes.first()
        if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE }) {
            val cleanParent = commit.parents.first { changes.none { change -> change.parentCommits.first() == it } }
            val lastChange = firstChange.file.getLastChange(cleanParent)!!
            firstChange.annotatedLines = lastChange.annotatedLines.map { AnnotatedLine(it.number, it.content) }
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
        // This is done temporarily until we figure out how to manage line changes in a merge commit
        firstChange.lineChanges = changes.flatMap { it.lineChanges }
        firstChange.lineChanges.filter { it.operation == LineOperation.ADD }.forEach { it.content = firstChange.annotatedLines[it.number - 1].content }
        firstChange.parentCommits = changes.flatMap { it.parentCommits }
        // till here

        // filter or mark changes so that we take into consideration only the relevant ones
        // maybe changes need to know where they come from
        LOG.info("Finished merging changes")
        return firstChange
    }
}
