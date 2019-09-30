package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.parsers.abstracts.ChangeParser

class MergeChangeParser(private val parentIndex: Int, private val numberOfParents: Int, parentCommitId: String) : ChangeParser(parentCommitId) {
    override fun addHunks(lines: MutableList<String>, changeDTO: ChangeDTO) {
        changeDTO.hunks = if (lines.isNotEmpty()) {
            getHunks(lines).map { MergeHunkParser(parentIndex, numberOfParents).parse(it) }
        } else emptyList()
    }

    override fun addAnnotatedLines(changeDTO: ChangeDTO) {

    }
}