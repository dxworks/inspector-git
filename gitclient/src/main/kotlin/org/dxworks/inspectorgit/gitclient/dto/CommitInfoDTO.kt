package org.dxworks.inspectorgit.gitclient.dto

open class CommitInfoDTO(val id: String,
                         val parentIds: List<String>,
                         val authorName: String,
                         val authorEmail: String,
                         val authorDate: String,
                         val committerName: String,
                         val committerEmail: String,
                         val committerDate: String,
                         val message: String)