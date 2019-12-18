package org.dxworks.inspectorgit.dto

import org.dxworks.inspectorgit.dto.SwProjectDTO

data class CreateSystemDTO(
        val name: String,
        val projects: List<SwProjectDTO>
)