package org.dxworks.inspectorgit.gitClient.dto

class CommitDTO(val id: String,
                val parentIds: List<String>,
                val authorName: String,
                val authorEmail: String,
                val authorDate: String,
                val committerName: String,
                val committerEmail: String,
                val committerDate: String,
                val message: String,
                val changes: List<ChangeDTO>)