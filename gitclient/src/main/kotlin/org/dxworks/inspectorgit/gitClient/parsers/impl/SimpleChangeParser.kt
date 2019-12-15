package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.parsers.abstracts.ChangeParser

class SimpleChangeParser(parentCommitId: String) : ChangeParser(parentCommitId) {
    override fun addAnnotatedLines(changeDTO: ChangeDTO) {}

    override fun addHunks(lines: List<String>, changeDTO: ChangeDTO) {
        changeDTO.hunks = if (lines.isNotEmpty())
            extractHunks(lines).map { SimpleHunkParser().parse(it) }
        else emptyList()
    }
}
