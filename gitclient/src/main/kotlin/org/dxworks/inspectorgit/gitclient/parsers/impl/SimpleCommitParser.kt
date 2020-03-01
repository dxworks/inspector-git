package org.dxworks.inspectorgit.gitclient.parsers.impl

import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitclient.parsers.abstracts.CommitParser

class SimpleCommitParser : CommitParser() {
    override fun getChanges(lines: List<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        return if (lines.isNotEmpty()) {
            extractChanges(lines).map { ChangeParser(parentIds.getOrElse(0) { "" }).parse(it) }
        } else ArrayList()
    }
}
