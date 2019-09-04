package org.dxworks.dto

import java.util.*

class CommitDTO(val id: String,
                val parentIds: List<String>,
                val authorName: String,
                val authorEmail: String,
                val date: Date,
                val message: String,
                val changes: List<ChangeDTO>) {
    val isMergeCommit: Boolean = parentIds.size > 1
}