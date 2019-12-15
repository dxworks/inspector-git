package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.dxworks.inspectorgit.gitClient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitClient.parsers.abstracts.CommitParser

class SimpleCommitParser : CommitParser() {
    override fun getChanges(lines: List<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        return if (lines.isNotEmpty()) {
            extractChanges(lines).map { SimpleChangeParser(parentIds.first()).parse(it) }
        } else ArrayList()
    }
}
