package org.dxworks.inspectorgit.client.parsers.impl

import org.dxworks.inspectorgit.client.dto.ChangeDTO
import org.dxworks.inspectorgit.client.parsers.abstracts.ChangeParser

class MergeChangeParser(private val parentIndex: Int, private val numberOfParents: Int, parentCommitId: String) : ChangeParser(parentCommitId) {
    override fun addAnnotatedLines(changeDTO: ChangeDTO) {}

    override fun addHunks(lines: List<String>, changeDTO: ChangeDTO) {
        changeDTO.hunks = if (lines.isNotEmpty())
            extractHunks(lines).map { MergeHunkParser(parentIndex, numberOfParents).parse(it) }
        else emptyList()
    }

}