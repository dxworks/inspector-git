package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.dxworks.inspectorgit.model.*
import org.slf4j.LoggerFactory

class ChangeTransformer(private val changeDTO: ChangeDTO, private val commit: Commit, private val project: Project, private val changeFactory: ChangeFactory) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeTransformer::class.java)
    }

    fun transform(): Change? {
        val parentCommit = if (changeDTO.parentCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.parentCommitId }!!
        val lastChange = if (changeDTO.type == ChangeType.ADD) null else getLastChange(parentCommit!!)
        LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
        val file = getFileForChange(changeDTO, project, lastChange)
        return changeFactory.create(
                commit = commit,
                type = changeDTO.type,
                oldFileName = changeDTO.oldFileName,
                newFileName = changeDTO.newFileName,
                file = file,
                parentCommit = parentCommit,
                lineChanges = getLineChanges(lastChange),
                parentChange = lastChange)
    }

    private tailrec fun getLastChange(parentCommit: Commit): Change {
        return parentCommit.changes.find { it.newFileName == changeDTO.oldFileName }
                ?: getLastChange(parentCommit.parents.first())
    }

    private fun getLineChanges(lastChange: Change?): MutableList<LineChange> {
        LOG.info("Calculating line changes")
        return changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, it.number, getContent(it, lastChange), commit) }.toMutableList()
    }

    private fun getContent(lineChangeDTO: LineChangeDTO, lastChange: Change?): AnnotatedContent {
        return if (lineChangeDTO.operation == LineOperation.ADD)
            AnnotatedContent(commit, lineChangeDTO.content)
        else {
            lastChange!!.annotatedLines[lineChangeDTO.number - 1].content
        }
    }

    private fun getFileForChange(change: ChangeDTO, project: Project, lastChange: Change?): File {
        LOG.info("Getting file")
        return if (change.type == ChangeType.ADD) {
            val newFile = File(change.isBinary)
            project.fileRegistry.add(newFile)
            newFile

        } else {
            lastChange!!.file
        }
    }
}