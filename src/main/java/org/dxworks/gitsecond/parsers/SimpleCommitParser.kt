package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.dto.ChangeDTO
import kotlin.collections.ArrayList

class SimpleCommitParser(lines: MutableList<String>) : CommitParser(lines) {
    override fun extractChanges(): List<ChangeDTO> {
        return if (lines.isNotEmpty()) {
            getChanges().map { SimpleChangeParser(it).parse().change }
        } else ArrayList()
    }
}
