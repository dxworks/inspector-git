package org.dxworks.inspectorgit.dto.localProjects

data class LocalSystemDTO(
        val id: String,
        val name: String,
        val sources: List<String>
)