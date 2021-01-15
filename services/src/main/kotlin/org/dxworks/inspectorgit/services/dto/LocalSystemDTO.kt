package org.dxworks.inspectorgit.services.dto

data class LocalSystemDTO(
        val id: String,
        val name: String,
        var sources: List<String>,
        val issues: List<String> = emptyList(),
        val remotes: List<String> = emptyList(),
        val computeAnnotatedLines: Boolean = true
)
