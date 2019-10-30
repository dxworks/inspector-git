package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.LineChangeDTO
import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.enums.LineOperation
import org.dxworks.inspectorgit.model.*
import org.dxworks.inspectorgit.registries.FileRegistry
import org.slf4j.LoggerFactory

class ChangeTransformer(private val changeDTO: ChangeDTO, private val commit: Commit, private val project: Project) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeTransformer::class.java)
    }

    fun transform(): Change {
        LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
        val annotatedLines = getAnnotatedLines(changeDTO, project)
        val file = getFileForChange(changeDTO, project)
        val parentCommit = if (changeDTO.parentCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.parentCommitId }!!
        val change = Change(
                commit = commit,
                type = changeDTO.type,
                file = file,
                parentCommit = parentCommit,
                oldFileName = changeDTO.oldFileName,
                newFileName = changeDTO.newFileName,
                lineChanges = getLineChanges(changeDTO, annotatedLines, commit, file, parentCommit),
                annotatedLines = annotatedLines)
        change.file.changes.add(change)
        LOG.info("Change created")
        return change
    }

    private fun getLineChanges(changeDTO: ChangeDTO, annotatedLines: MutableList<AnnotatedLine>, commit: Commit, file: File, parentCommit: Commit?): MutableList<LineChange> {
        return changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, getAnnotatedLine(it, annotatedLines, commit, file, parentCommit)) }.toMutableList()
    }

    private fun getAnnotatedLine(lineChangeDTO: LineChangeDTO, annotatedLines: MutableList<AnnotatedLine>, commit: Commit, file: File, parentCommit: Commit?): AnnotatedLine {
        return if (lineChangeDTO.operation == LineOperation.ADD)
            annotatedLines.getOrElse(lineChangeDTO.number) {
                AnnotatedLine(commit, lineChangeDTO.number, lineChangeDTO.content)
            }
        else file.getLastChange(parentCommit)!!.annotatedLines[lineChangeDTO.number - 1]
    }

    private fun getAnnotatedLines(changeDTO: ChangeDTO, project: Project): MutableList<AnnotatedLine> {
        LOG.info("Calculating annotated lines")
        return changeDTO.annotatedLines.map { AnnotatedLine(project.commitRegistry.getByID(it.commitId)!!, it.number, it.content) }.toMutableList()
    }

    private fun getFileForChange(change: ChangeDTO, project: Project): File {
        LOG.info("Getting file")
        return when (change.type) {
            ChangeType.ADD -> {
                val file = File(change.isBinary)
                project.fileRegistry.add(file, change.newFileName)
                file
            }
            ChangeType.RENAME -> {
                val file = getFileForChange(change.oldFileName, project.commitRegistry.getByID(change.parentCommitId)!!, project.fileRegistry)
                project.fileRegistry.add(file, change.newFileName)
                file
            }
            else -> {
                getFileForChange(change.oldFileName, project.commitRegistry.getByID(change.parentCommitId)!!, project.fileRegistry)
            }
        }
    }

    private tailrec fun getFileForChange(name: String, commit: Commit?, fileRegistry: FileRegistry): File {
        if (commit == null)
            return fileRegistry.getByID(name)!!
        val change = commit.changes.find { it.newFileName == name }
        return change?.file ?: getFileForChange(name, commit.parents.firstOrNull(), fileRegistry)
    }
}