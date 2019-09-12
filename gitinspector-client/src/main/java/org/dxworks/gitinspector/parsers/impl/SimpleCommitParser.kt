package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.dto.ChangeDTO
import org.dxworks.gitinspector.parsers.abstracts.CommitParser

class SimpleCommitParser : CommitParser() {
    override fun extractChanges(lines: MutableList<String>, commitId: String, parentIds: List<String>): List<ChangeDTO> {
        return if (lines.isNotEmpty()) {
            getChanges(lines).map { SimpleChangeParser(commitId).parse(it) }
        } else ArrayList()
    }
}
