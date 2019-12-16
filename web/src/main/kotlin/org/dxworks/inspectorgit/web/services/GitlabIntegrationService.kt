package org.dxworks.inspectorgit.web.services

import org.dxworks.inspectorgit.dto.GitlabCredentialsDTO
import org.dxworks.inspectorgit.dto.ImportGitlabProjectsDTO
import org.dxworks.inspectorgit.services.ProjectService
import org.dxworks.inspectorgit.web.dto.GitlabSimpleProjectResponseDTO
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.exchange

@Service
class GitlabIntegrationService(private val projectService: ProjectService) {
    private val restTemplate = RestTemplate()
    private val defaultGitlabUrl = "https://gitlab.com"
    private val apiUrl = "/api/v4/"

    private val integrationName = "gitlab"


    fun listRepositories(gitlabCredentialsDTO: GitlabCredentialsDTO): List<GitlabSimpleProjectResponseDTO>? {
        val url = (gitlabCredentialsDTO.url ?: defaultGitlabUrl) + apiUrl + "projects?simple=true"
        return restTemplate.exchange<List<GitlabSimpleProjectResponseDTO>>(url, HttpMethod.GET, HttpEntity(null, getHeaders(gitlabCredentialsDTO.token))).body
    }

    private fun getHeaders(token: String): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.set("Private-token", token)
        return httpHeaders
    }

    fun import(importProjectsDTO: ImportGitlabProjectsDTO) {
        importProjectsDTO.projects.parallelStream().forEach {
            it.path = "$integrationName/${it.path}"
            projectService.import(it, importProjectsDTO.credentials.username, importProjectsDTO.credentials.token)
        }
    }


}