package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.parsers.abstracts.CommitParser

class MergeCommitParser : CommitParser() {

    override fun getChanges(lines: List<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        return if (lines.isNotEmpty()) getMergeChanges(lines, parentIds) else emptyList()
    }

    private fun getMergeChanges(lines: List<String>, parentIds: List<String>): List<ChangeDTO> {
        val changes = extractChanges(lines)
        return parentIds.mapIndexed { index, parentCommitId -> changes.map { MergeChangeParser(index, parentIds.size, parentCommitId).parse(it) } }.flatten()
    }
}