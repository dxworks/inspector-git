package org.dxworks.dto

import org.dxworks.gitsecond.model.AuthorID
import java.util.*

class CommitDTO(val commitId: String,
                val parentIds: List<String>,
                val authorId: AuthorID,
                val date: Date,
                val message: String,
                val changes: List<ChangeDTO>) {
    val isMergeCommit: Boolean = parentIds.size > 1
}