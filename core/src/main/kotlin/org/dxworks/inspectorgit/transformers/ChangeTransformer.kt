package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.dto.LineChangeDTO
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.gitClient.enums.LineOperation
import org.dxworks.inspectorgit.model.*
import org.dxworks.inspectorgit.registries.FileRegistry
import org.slf4j.LoggerFactory

class ChangeTransformer(private val changeDTO: ChangeDTO, private val commit: Commit, private val project: Project) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeTransformer::class.java)
    }

    fun transform(): Change {
        LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
        val file = getFileForChange(changeDTO, project, commit.isMergeCommit)
        val parentCommit = if (changeDTO.parentCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.parentCommitId }!!
        val change = Change(
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
    }

    private fun getLineChanges(changeDTO: ChangeDTO, commit: Commit, file: File, parentCommit: Commit?): MutableList<LineChange> {
        LOG.info("Calculating line changes")
        return changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, getAnnotatedLine(it, commit, file, parentCommit), commit) }.toMutableList()
    }

    private fun getAnnotatedLine(lineChangeDTO: LineChangeDTO, commit: Commit, file: File, parentCommit: Commit?): AnnotatedLine {
        return if (lineChangeDTO.operation == LineOperation.ADD)
            AnnotatedLine(commit, lineChangeDTO.number, lineChangeDTO.content)
        else {
            val lastChange = file.getLastChange(parentCommit)!!
            lastChange.annotatedLines[lineChangeDTO.number - 1]
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