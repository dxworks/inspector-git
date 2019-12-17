package org.dxworks.inspectorgit.web.dto

import org.dxworks.inspectorgit.dto.SwProjectDTO

data class CreateSystemDTO(
        val name: String,
        val projects: List<SwProjectDTO>
)