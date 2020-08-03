package org.dxworks.inspectorgit.dto.localProjects

data class LocalSystemDTO(
        val id: String,
        val name: String,
        var sources: List<String>,
        val issues: List<String>,
        val remotes: List<String>
)