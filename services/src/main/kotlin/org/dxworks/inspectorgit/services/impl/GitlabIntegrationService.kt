package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.persistence.entities.SwProjectEntity
import org.dxworks.inspectorgit.services.IntegrationService
import org.dxworks.inspectorgit.services.ProjectService
import org.dxworks.inspectorgit.services.dto.GitlabSimpleProjectResponseDTO
import org.dxworks.inspectorgit.services.dto.SwProjectDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange
import kotlin.streams.toList

@Service
class GitlabIntegrationService(private val integrationService: IntegrationService,
                               private val projectService: ProjectService) {
    private val restTemplate = RestTemplate()
    private val defaultGitlabUrl = "https://gitlab.com"
    private val apiUrl = "/api/v4/"

    private val platform = "gitlab"


    fun listProjects(integrationName: String): List<SwProjectDTO>? {
        val integrationDTO = integrationService.findByNameAndPlatform(integrationName, platform)
        val url = "${integrationDTO.url}${apiUrl}projects?simple=true"
        val gitlabProjectResponseDTOs = restTemplate.exchange<List<GitlabSimpleProjectResponseDTO>>(url, HttpMethod.GET, HttpEntity(null, getHeaders(integrationDTO.password!!))).body
        return gitlabProjectResponseDTOs?.map {
            val swProjectDTO = SwProjectDTO()
            swProjectDTO.name = it.name
            swProjectDTO.platform = platform
            swProjectDTO.integrationName = integrationDTO.name
            swProjectDTO.webUrl = it.webUrl
            swProjectDTO.description = it.description
            swProjectDTO.path = it.path
            swProjectDTO.branch = it.defaultBranch
            swProjectDTO.repositoryHttpUrl = it.httpUrlToRepo
            swProjectDTO.imported = projectService.existsByPath("$platform/${it.path}")
            swProjectDTO
        }
    }

    private fun getHeaders(token: String): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.set("Private-token", token)
        return httpHeaders
    }

    fun import(projectsDTO: List<SwProjectDTO>): List<SwProjectEntity> {
        return projectsDTO.parallelStream()
                .filter { !projectService.existsByPath("$platform/${it.path}") }
                .map {
                    it.path = "$platform/${it.path}"
                    val integrationDTO = integrationService.findByNameAndPlatform(it.integrationName!!, it.platform!!)
                    projectService.import(it, integrationDTO.username, integrationDTO.password!!)
                }.toList()
    }


}