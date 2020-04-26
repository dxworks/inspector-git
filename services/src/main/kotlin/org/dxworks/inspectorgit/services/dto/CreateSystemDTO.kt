package org.dxworks.inspectorgit.services.dto

data class CreateSystemDTO(
        val name: String,
        val projects: List<SwProjectDTO>
)