package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.HunkDTO
import org.dxworks.inspectorgit.parsers.abstracts.ChangeParser

class SimpleChangeParser(parentCommitId: String) : ChangeParser(parentCommitId) {
    override fun addAnnotatedLines(changeDTO: ChangeDTO) {}

    override fun addHunks(lines: List<String>, changeDTO: ChangeDTO) {
        changeDTO.hunks = if (lines.isNotEmpty()) {
            getHunks(lines).map { SimpleHunkParser().parse(it) }
        } else emptyList()
    }
}
