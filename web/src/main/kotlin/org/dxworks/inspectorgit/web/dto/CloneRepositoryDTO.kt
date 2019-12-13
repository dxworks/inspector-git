package org.dxworks.inspectorgit.web.dto

data class CloneRepositoryDTO(val url: String,
                              val repoName: String,
                              val branch: String)