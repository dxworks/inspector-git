package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.dto.LineChangeDTO
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.gitClient.enums.LineOperation
import org.dxworks.inspectorgit.model.*
import org.dxworks.inspectorgit.registries.FileRegistry
import org.slf4j.LoggerFactory

class ChangeTransformer(private val changeDTO: ChangeDTO, private val commit: Commit, private val project: Project, private val changeFactory: ChangeFactory) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeTransformer::class.java)
    }

    fun transform(): Change? {
        LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
        val file = getFileForChange(changeDTO, project, commit.isMergeCommit)
        val parentCommit = if (changeDTO.parentCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.parentCommitId }!!
        try {
            val change = changeFactory.create(
                    commit = commit,
                    type = changeDTO.type,
                    file = file,
                    parentCommits = if (parentCommit == null) emptyList() else listOf(parentCommit),
                    oldFileName = changeDTO.oldFileName,
                    newFileName = changeDTO.newFileName,
                    lineChanges = getLineChanges(changeDTO, commit, file, parentCommit),
                    parentCommit = parentCommit)
            change.file.changes.add(change)
            LOG.info("Change created")
            return change
        } catch (e: IllegalStateException) {
            LOG.warn(e.message)
            return null
        }
    }

    private fun getLineChanges(changeDTO: ChangeDTO, commit: Commit, file: File, parentCommit: Commit?): MutableList<LineChange> {
        LOG.info("Calculating line changes")
        val lastChange = if (parentCommit == null) null else file.getLastChange(parentCommit)
        return changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, getAnnotatedLine(it, commit, lastChange), commit) }.toMutableList()
    }

    private fun getAnnotatedLine(lineChangeDTO: LineChangeDTO, commit: Commit, lastChange: Change?): AnnotatedLine {
        return if (lineChangeDTO.operation == LineOperation.ADD)
            AnnotatedLine(commit, lineChangeDTO.number, lineChangeDTO.content)
        else {
            if (lastChange == null)
                throw IllegalStateException("Last change is null")
            else {
                val annotatedLine = lastChange.annotatedLines[lineChangeDTO.number - 1]
                return if (annotatedLine.content != lineChangeDTO.content) {
                    val errorInfo = "in file ${changeDTO.newFileName} at line ${lineChangeDTO.number}.\nExpected: ${lineChangeDTO.content}\nActual: ${annotatedLine.content}"
                    if (lastChange.annotatedLines.size > lineChangeDTO.number) {
                        LOG.debug("Line contents don't match but content was fount on next line. Picking that line instead.\n$errorInfo")
                        lastChange.annotatedLines[lineChangeDTO.number]
                    } else {
                        LOG.error("Line contents don't match $errorInfo")
                        annotatedLine
                    }
                } else
                    annotatedLine
            }
        }
    }

    private fun getFileForChange(change: ChangeDTO, project: Project, mergeCommit: Boolean): File {
        LOG.info("Getting file")
        return when (change.type) {
            ChangeType.ADD -> {
                val file = project.fileRegistry.getById(change.newFileName)
                if (mergeCommit && file != null) {
                    file
                } else {
                    val newFile = File(change.isBinary)
                    project.fileRegistry.add(newFile, change.newFileName)
                    newFile
                }
            }
            ChangeType.RENAME -> {
                if (mergeCommit) {
                    project.fileRegistry.getById(change.newFileName)!!
                } else {
                    val file = getFileForChange(change.oldFileName, project.commitRegistry.getById(change.parentCommitId)!!, project.fileRegistry)
                            ?: getFileAndSolveCaseOnlyRename(change)
                    project.fileRegistry.add(file, change.newFileName)
                    file
                }
            }
            else -> {
                val file = if (mergeCommit)
                    project.fileRegistry.getById(change.oldFileName)
                else
                    getFileForChange(change.oldFileName, project.commitRegistry.getById(change.parentCommitId)!!, project.fileRegistry)

                file ?: getFileAndSolveCaseOnlyRename(change)
            }
        }
    }

    private fun getFileAndSolveCaseOnlyRename(change: ChangeDTO): File {
        val file = project.fileRegistry.getByIdInsensitive(change.oldFileName)!!
        change.type = ChangeType.RENAME
        change.oldFileName = file.fullyQualifiedName!!
        project.fileRegistry.add(file, change.newFileName)
        return file
    }

    private tailrec fun getFileForChange(name: String, commit: Commit?, fileRegistry: FileRegistry): File? {
        if (commit == null)
            return fileRegistry.getById(name)
        val change = commit.changes.find { it.newFileName == name }
        return change?.file ?: getFileForChange(name, commit.parents.firstOrNull(), fileRegistry)
    }
}