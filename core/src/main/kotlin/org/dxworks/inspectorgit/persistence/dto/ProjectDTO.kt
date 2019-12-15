package org.dxworks.inspectorgit.persistence.dto

import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO

data class ProjectDTO(
        val name: String,
        val path: String,
        val branch: String,
        val integrationPath: String,
        val repositoryHttpUrl: String,
        val webUrl: String,
        val pullRequestsEnabled: Boolean,
        var gitLogDTO: GitLogDTO? = null
)
