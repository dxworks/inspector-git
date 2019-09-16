package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.parsers.abstracts.CommitParser

class SimpleCommitParser : CommitParser() {
    override fun extractChanges(lines: MutableList<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        return if (lines.isNotEmpty()) {
            getChanges(lines).map { SimpleChangeParser(parentIds.first()).parse(it) }
        } else ArrayList()
    }
}
