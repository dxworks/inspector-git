package org.dxworks.inspectorgit.web.dto

data class CloneRepositoryDTO(val url: String,
                              val path: String,
                              val branch: String)