package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.HunkDTO
import org.dxworks.inspectorgit.parsers.abstracts.ChangeParser

class SimpleChangeParser(otherCommitId: String) : ChangeParser(otherCommitId) {
    override fun addAnnotatedLines(changeDTO: ChangeDTO) {}

    override fun addHunks(lines: MutableList<String>, changeDTO: ChangeDTO) {
        changeDTO.hunks = if (lines.isNotEmpty()) {
            getHunks(lines).map { SimpleHunkParser().parse(it) }
        } else emptyList()
    }
}
