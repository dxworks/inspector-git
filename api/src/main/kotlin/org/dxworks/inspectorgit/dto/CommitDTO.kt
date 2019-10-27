package org.dxworks.inspectorgit.dto

import java.util.*

class CommitDTO(val id: String,
                val parentIds: List<String>,
                val authorName: String,
                val authorEmail: String,
                val authorDate: Date,
                val committerName: String,
                val committerEmail: String,
                val committerDate: Date,
                val message: String,
                val changes: List<ChangeDTO>)