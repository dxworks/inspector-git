package org.dxworks.inspectorgit.web.dto

import org.dxworks.inspectorgit.dto.ImportGitlabProjectsDTO

data class CreateSystemDTO(
        val name: String,
        val gitlabIntegrationProjectsDTO: ImportGitlabProjectsDTO
)