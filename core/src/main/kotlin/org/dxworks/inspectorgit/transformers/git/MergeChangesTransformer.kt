package org.dxworks.inspectorgit.transformers.git

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.model.git.Change
import org.dxworks.inspectorgit.model.git.ChangeType
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitProject
import org.slf4j.LoggerFactory

class MergeChangesTransformer {

    companion object {
        val LOG = LoggerFactory.getLogger(MergeChangesTransformer::class.java)

        fun transform(changeDTOs: List<ChangeDTO>, commit: Commit, project: GitProject, computeAnnotatedLines: Boolean, changeFactory: ChangeFactory): List<Change> {
            val changes = changeDTOs.mapNotNull { ChangeTransformer.transform(it, commit, project, computeAnnotatedLines, changeFactory) }
            return if (changes.isEmpty()) return emptyList() else fixChanges(changes, commit, project)
        }

        private fun fixChanges(changes: List<Change>, commit: Commit, project: GitProject): List<Change> {
            LOG.debug("Merging ${changes.size} changes")

            val missingChange = if (changes.size < commit.parents.size && !changes.all { it.type == ChangeType.DELETE })
                getMissingChange(changes, commit) else null
            fixAnnotatedLinesCommits(changes, missingChange, commit)
            mergeFiles(changes, missingChange, project)

            LOG.debug("Finished merging changes")
            return changes
        }

        private fun getMissingChange(changes: List<Change>, commit: Commit): Change {
            val cleanParent = commit.parents.first { changes.none { change -> change.parentCommit == it } }
            return ChangeTransformer.getLastChange(cleanParent, changes.first().newFileName)
        }

        private fun mergeFiles(changes: List<Change>, missingChange: Change?, project: GitProject) {
            val files = (changes.map { it.file } + listOf(missingChange?.file)).filterNotNull().distinct()
            if (files.size > 1) {
                val allFileChanges = files.flatMap { it.changes }.distinct()
                val file = files.first()
                file.changes = allFileChanges.sortedBy { it.commit.committerDate }.toMutableList()
                changes.forEach { it.file = file }

                files.drop(1).forEach { project.fileRegistry.delete(it) }
            }
        }

        private fun fixAnnotatedLinesCommits(changes: List<Change>, missingChange: Change?, commit: Commit) {
            missingChange?.let { changes.first().annotatedLines = it.annotatedLines }

            val annotatedFiles = changes.map { it.annotatedLines }
            for (i in annotatedFiles.first().indices) {
                val currentAnnotatedLines = annotatedFiles.map { it[i] }
                val firstAnnotatedLine = currentAnnotatedLines[0]
                val annotatedLines = currentAnnotatedLines.drop(1)
                if (firstAnnotatedLine == commit)
                    annotatedLines.find { it != commit }?.let { annotatedFiles[0].set(i, it) }
            }
            changes.drop(1).forEach { it.annotatedLines = changes.first().annotatedLines }
        }
    }
}
