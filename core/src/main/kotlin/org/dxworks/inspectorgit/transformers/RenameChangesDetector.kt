package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

class RenameChangesDetector(private val changes: List<ChangeDTO>, private val project: Project) {
    companion object {
        private val LOG = LoggerFactory.getLogger(RenameChangesDetector::class.java)
    }

    fun detectAndReplace(): List<ChangeDTO> {

        LOG.info("Getting potentially renamed files")

        val potentiallyRenamedFiles = changes.filter { it.type == ChangeType.ADD }.mapNotNull { project.fileRegistry.getByID(it.newFileName) }

        LOG.info("Found: ${potentiallyRenamedFiles.size}")
        LOG.info("Getting rename changes")

        val renameChanges = potentiallyRenamedFiles.mapNotNull { it.changes.find { change -> change.isRenameChange } }
        val newFileNames = renameChanges.map { it.newFileName }
        val oldFileNames = renameChanges.map { it.oldFileName }

        LOG.info("Found: ${renameChanges.size}")
        LOG.info("filtering legit changes")

        val acceptedChanges = changes.filter { !(isFakeAdd(it, newFileNames) || isFakeDelete(it, oldFileNames)) }
        val fakeAddChanges = changes.filter { isFakeAdd(it, newFileNames) }

        LOG.info("Found ${acceptedChanges.size} accepted changes")
        LOG.info("Substituting ${fakeAddChanges.size} add-delete pairs with rename changes")

        return acceptedChanges + fakeAddChanges.map {
            val renameChange = renameChanges.find { rc -> rc.newFileName == it.newFileName }!!
            ChangeDTO(oldFileName = renameChange.oldFileName,
                    newFileName = renameChange.newFileName,
                    type = ChangeType.RENAME,
                    parentCommitId = it.parentCommitId,
                    isBinary = it.isBinary,
                    hunks = emptyList(),
                    annotatedLines = it.annotatedLines,
                    isBlame = it.isBlame)
        }
    }

    private fun isFakeDelete(it: ChangeDTO, oldFileNames: List<String>) =
            (it.type == ChangeType.DELETE && oldFileNames.contains(it.oldFileName))

    private fun isFakeAdd(it: ChangeDTO, newFileNames: List<String>) =
            (it.type == ChangeType.ADD && newFileNames.contains(it.newFileName))
}