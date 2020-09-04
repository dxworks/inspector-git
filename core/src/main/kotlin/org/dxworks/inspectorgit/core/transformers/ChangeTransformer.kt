package org.dxworks.inspectorgit.core.transformers

import org.dxworks.inspectorgit.core.ChangeFactory
import org.dxworks.inspectorgit.core.model.*
import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.slf4j.LoggerFactory

class ChangeTransformer {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeTransformer::class.java)

        tailrec fun getLastChange(parentCommit: Commit, fileName: String): Change {
            return parentCommit.changes.find { it.newFileName == fileName }
                    ?: getLastChange(parentCommit.parents.first(), fileName)
        }

        fun transform(changeDTO: ChangeDTO, commit: Commit, project: Project, changeFactory: ChangeFactory): Change? {
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
                    hunks = getHunks(lastChange, changeDTO, commit),
                    parentChange = lastChange)
        }

        private fun getHunks(lastChange: Change?, changeDTO: ChangeDTO, commit: Commit): List<Hunk> {
            LOG.info("Calculating line changes")
            if (lastChange != null && lastChange.file.isBinary)
                return emptyList()
            return changeDTO.hunks.map { Hunk(it.lineChanges.map { LineChange(it.operation, it.number, commit) }) }
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
}
