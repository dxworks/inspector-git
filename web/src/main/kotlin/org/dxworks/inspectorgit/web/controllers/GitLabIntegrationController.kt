package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.services.RepositoryService
import org.dxworks.inspectorgit.web.apiPath
import org.dxworks.inspectorgit.web.dto.GitlabCloneRepositoriesDTO
import org.dxworks.inspectorgit.web.dto.GitlabCredentialsDTO
import org.dxworks.inspectorgit.web.dto.GitlabSimpleRepositoryResponseDTO
import org.dxworks.inspectorgit.web.services.GitlabIntegrationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$apiPath/gitlab")
class GitLabIntegrationController(private val repositoryService: RepositoryService,
                                  private val gitlabIntegrationService: GitlabIntegrationService) {
    @GetMapping("cloneRepositories")
    fun cloneRepositories(@RequestBody cloneRepositoriesDTO: GitlabCloneRepositoriesDTO) {
        cloneRepositoriesDTO.repositories.forEach {
            repositoryService.cloneRepository(it.url, it.repoName, it.branch, cloneRepositoriesDTO.credentials.username, cloneRepositoriesDTO.credentials.token)
        }
    }

    @GetMapping("listRepositories")
    fun listRepositories(@RequestBody gitlabCredentialsDTO: GitlabCredentialsDTO): List<GitlabSimpleRepositoryResponseDTO>? {
        return gitlabIntegrationService.listRepositories(gitlabCredentialsDTO)
    }
}