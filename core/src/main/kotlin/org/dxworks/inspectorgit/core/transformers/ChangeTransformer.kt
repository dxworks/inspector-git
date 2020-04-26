package org.dxworks.inspectorgit.core.transformers

import org.dxworks.inspectorgit.core.ChangeFactory
import org.dxworks.inspectorgit.core.model.*
import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.slf4j.LoggerFactory

class ChangeTransformer(private val changeDTO: ChangeDTO, private val commit: Commit, private val project: Project, private val changeFactory: ChangeFactory) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeTransformer::class.java)

        tailrec fun getLastChange(parentCommit: Commit, fileName: String): Change {
            return parentCommit.changes.find { it.newFileName == fileName }
                    ?: getLastChange(parentCommit.parents.first(), fileName)
        }
    }

    fun transform(): Change? {
        val parentCommit = if (changeDTO.parentCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.parentCommitId }!!
        val lastChange = if (changeDTO.type == ChangeType.ADD) null else getLastChange(parentCommit!!, changeDTO.oldFileName)
        LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
        val file = getFileForChange(changeDTO, project, lastChange)
        return changeFactory.create(
                commit = commit,
                type = changeDTO.type,
                oldFileName = changeDTO.oldFileName,
                newFileName = changeDTO.newFileName,
                file = file,
                parentCommit = parentCommit,
                hunks = getHunks(lastChange),
                parentChange = lastChange)
    }

    private fun getHunks(lastChange: Change?): List<Hunk> {
        LOG.info("Calculating line changes")
        return changeDTO.hunks.map { Hunk(it.lineChanges.map { LineChange(it.operation, getAnnotatedLine(it, lastChange), commit) }) }
    }

    private fun getAnnotatedLine(lineChangeDTO: LineChangeDTO, lastChange: Change?): AnnotatedLine {
        return if (lineChangeDTO.operation == LineOperation.ADD)
            AnnotatedLine(lineChangeDTO.number, commit)
        else {
            lastChange!!.annotatedLines[lineChangeDTO.number - 1]
        }
    }

    private fun getFileForChange(change: ChangeDTO, project: Project, lastChange: Change?): File {
        LOG.info("Getting file")
        return if (change.type == ChangeType.ADD) {
            val newFile = File(change.isBinary, project)
            project.fileRegistry.add(newFile)
            newFile

        } else {
            lastChange!!.file
        }
    }
}