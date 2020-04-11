package org.dxworks.inspectorgit.dto.localProjects

data class LocalProjectDTO(
        val id: String,
        val name: String,
        val sources: List<String>
)