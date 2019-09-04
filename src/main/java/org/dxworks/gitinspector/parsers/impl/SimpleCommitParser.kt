package org.dxworks.gitinspector.parsers.impl

import org.dxworks.dto.ChangeDTO
import org.dxworks.gitinspector.parsers.abstracts.CommitParser

class SimpleCommitParser : CommitParser() {
    override fun extractChanges(lines: MutableList<String>, commitId: String): List<ChangeDTO> {
        return if (lines.isNotEmpty()) {
            getChanges(lines).map { SimpleChangeParser().parse(lines) }
        } else ArrayList()
    }
}