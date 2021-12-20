package org.dxworks.inspectorgit.gitclient.dto

import com.fasterxml.jackson.annotation.JsonIgnore

open class CommitInfoDTO(val id: String,
                         val parentIds: List<String>,
                         val authorName: String,
                         val authorEmail: String,
                         val authorDate: String,
                         @JsonIgnore
                         val committerName: String,
                         @JsonIgnore
                         val committerDate: String,
                         @JsonIgnore
                         val committerEmail: String,
                         val message: String)
