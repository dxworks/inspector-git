package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.parsers.abstracts.CommitParser

class SimpleCommitParser : CommitParser() {
    override fun getChanges(lines: List<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        return if (lines.isNotEmpty()) {
            extractChanges(lines).map { SimpleChangeParser(parentIds.first()).parse(it) }
        } else ArrayList()
    }
}
