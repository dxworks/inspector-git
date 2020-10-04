package org.dxworks.inspectorgit.transformers.git

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.model.git.*
import org.slf4j.LoggerFactory

class ChangeTransformer {
    companion object {
        private val LOG = LoggerFactory.getLogger(ChangeTransformer::class.java)

        tailrec fun getLastChange(parentCommit: Commit, fileName: String): Change {
            return parentCommit.changes.find { it.newFileName == fileName }
                    ?: getLastChange(parentCommit.parents.first(), fileName)
        }

        fun transform(changeDTO: ChangeDTO, commit: Commit, project: GitProject, changeFactory: ChangeFactory): Change? {
            val parentCommit = if (changeDTO.parentCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.parentCommitId }!!
            val lastChange = if (changeDTO.type == org.dxworks.inspectorgit.gitclient.enums.ChangeType.ADD) null else getLastChange(parentCommit!!, changeDTO.oldFileName)
            LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
            val file = getFileForChange(changeDTO, lastChange, project)
            return changeFactory.create(
                    commit = commit,
                    type = ChangeType.valueOf(changeDTO.type.name),
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
            return changeDTO.hunks.map { Hunk(it.lineChanges.map { LineChange(LineOperation.valueOf(it.operation.name), it.number, commit) }) }
        }

        private fun getFileForChange(change: ChangeDTO, lastChange: Change?, project: GitProject): File {
            LOG.info("Getting file")
            return if (change.type == org.dxworks.inspectorgit.gitclient.enums.ChangeType.ADD) {
                val newFile = File(change.isBinary, project)
                project.fileRegistry.add(newFile)
                newFile

            } else {
                lastChange!!.file
            }
        }
    }
}
