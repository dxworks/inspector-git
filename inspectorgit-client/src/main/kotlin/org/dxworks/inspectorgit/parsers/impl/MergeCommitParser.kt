package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.GitClient
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.parsers.abstracts.CommitParser

class MergeCommitParser(private val gitClient: GitClient) : CommitParser() {

    override fun getChanges(lines: List<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        val listOfParentAndFile: List<Pair<String, String>> = getAffectedFilesByParent(commitId, parentIds)
        val blameChanges = getBlameChanges(listOfParentAndFile, commitId)
        val mergeChanges = if (lines.isNotEmpty()) getMergeChanges(lines, parentIds) else emptyList()
        return blameChanges + mergeChanges
    }

    private fun getBlameChanges(parentAndFile: List<Pair<String, String>>, commitId: String): List<ChangeDTO> {
        return parentAndFile.map { Pair(it.first, gitClient.diff(it.first, commitId, it.second)) }
                .filter { it.second.isNotEmpty() }
                .map { BlameParser(gitClient, commitId, it.first).parse(it.second) }
    }

    private fun getMergeChanges(lines: List<String>, parentIds: List<String>): List<ChangeDTO> {
        val changes = extractChanges(lines)
        return parentIds.mapIndexed { index, parentCommitId -> changes.map { MergeChangeParser(index, parentIds.size, parentCommitId).parse(it) } }.flatten()
    }

    private fun getAffectedFilesByParent(commitId: String, parentIds: List<String>): List<Pair<String, String>> {
        val affectedFiles = gitClient.affectedFiles(commitId)

        val parentAndFile: MutableList<Pair<String, String>> = ArrayList()
        var parentIndex = 0
        for (fileName in affectedFiles) {
            if (fileName.isNotBlank())
                parentAndFile.add(Pair(parentIds[parentIndex], fileName))
            else
                parentIndex++
        }
        return parentAndFile
    }
}