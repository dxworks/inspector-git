package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.dto.HunkDTO
import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.model.Project
import org.eclipse.jgit.diff.HistogramDiff
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import org.slf4j.LoggerFactory

class RenameChangesDetector(private val changes: List<ChangeDTO>, private val project: Project) {
    companion object {
        private val LOG = LoggerFactory.getLogger(RenameChangesDetector::class.java)
    }

    fun detectAndReplace(): List<ChangeDTO> {

        LOG.info("Getting potentially renamed files")

        val potentiallyRenamedFiles = changes.filter { it.type == ChangeType.ADD }.mapNotNull { project.fileRegistry.getById(it.newFileName) }

        LOG.info("Found: ${potentiallyRenamedFiles.size}")
        LOG.info("Getting rename changes")

        val renameChanges = potentiallyRenamedFiles.mapNotNull { it.changes.find { change -> change.isRenameChange } }
        val newFileNames = renameChanges.map { it.newFileName }
        val oldFileNames = renameChanges.map { it.oldFileName }

        LOG.info("Found: ${renameChanges.size}")
        LOG.info("filtering legit changes")


        val acceptedChanges = changes.filter { !(isFakeAdd(it, newFileNames) || isFakeDelete(it, oldFileNames)) }
        val fakeAddChanges = changes.filter { isFakeAdd(it, newFileNames) }
        val fakeDeleteChanges = changes.filter { isFakeDelete(it, oldFileNames) }

        LOG.info("Found ${acceptedChanges.size} accepted changes")
        LOG.info("Substituting ${fakeAddChanges.size} add-delete pairs with rename changes")

        return acceptedChanges + fakeAddChanges.map {
            val renameChange = renameChanges.find { rc -> rc.newFileName == it.newFileName }!!
            val deleteChange = fakeDeleteChanges.find { dc -> dc.oldFileName == it.oldFileName }!!
            ChangeDTO(oldFileName = renameChange.oldFileName,
                    newFileName = renameChange.newFileName,
                    type = ChangeType.RENAME,
                    parentCommitId = it.parentCommitId,
                    isBinary = it.isBinary,
                    hunks = listOf(getHunk(it, deleteChange))) // TODO: fix this (hunks should be difference between add and delete, or only add changes and calculate true line owner from last change)
        }
    }

    private fun getHunk(addChange: ChangeDTO, deleteChange: ChangeDTO): HunkDTO {
        var addLineIndex = 0
        var removeLineIndex = 0
        val addLines = addChange.hunks[0].lineChanges
        val deletedLines = deleteChange.hunks[0].lineChanges
        val from = RawText(deletedLines.joinToString("\n") { it.content }.toByteArray())
        val to = RawText(addLines.joinToString("\n") { it.content }.toByteArray())
        val diff = HistogramDiff().diff(RawTextComparator.DEFAULT, from, to)
        return HunkDTO(emptyList())
    }

    private fun isFakeDelete(it: ChangeDTO, oldFileNames: List<String>) =
            (it.type == ChangeType.DELETE && oldFileNames.contains(it.oldFileName))

    private fun isFakeAdd(it: ChangeDTO, newFileNames: List<String>) =
            (it.type == ChangeType.ADD && newFileNames.contains(it.newFileName))
}