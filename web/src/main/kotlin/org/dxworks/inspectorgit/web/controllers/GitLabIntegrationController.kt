package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.persistence.dto.ProjectDTO
import org.dxworks.inspectorgit.services.ProjectService
import org.dxworks.inspectorgit.web.apiPath
import org.dxworks.inspectorgit.web.dto.GitlabCloneRepositoriesDTO
import org.dxworks.inspectorgit.web.dto.GitlabCredentialsDTO
import org.dxworks.inspectorgit.web.dto.GitlabSimpleProjectResponseDTO
import org.dxworks.inspectorgit.web.services.GitlabIntegrationService
import org.springframework.web.bind.annotation.*

@CrossOrigin(origins = ["http://localhost:4200"], maxAge = 3600)
@RestController
@RequestMapping("$apiPath/gitlab")
class GitLabIntegrationController(private val gitlabIntegrationService: GitlabIntegrationService,
                                  private val projectService: ProjectService) {
    private val integrationName = "gitlab"

    @PutMapping("importProjects")
    fun importProjects(@RequestBody cloneRepositoriesDTO: GitlabCloneRepositoriesDTO) {
        cloneRepositoriesDTO.projects.parallelStream().forEach {
            projectService.create(it, cloneRepositoriesDTO.credentials.username, cloneRepositoriesDTO.credentials.token)
        }
    }

    @PutMapping("listProjects")
    fun listProjects(@RequestBody gitlabCredentialsDTO: GitlabCredentialsDTO): List<GitlabSimpleProjectResponseDTO>? {
        return gitlabIntegrationService.listRepositories(gitlabCredentialsDTO)
    }

    @DeleteMapping("deleteProject")
    fun deleteProject(@RequestParam(required = true) path: String) {
        projectService.delete("$integrationName/$path")
    }

    @GetMapping("projects")
    fun getProjects(): List<ProjectDTO> {
        return this.projectService.getProjectsWithoutLog()
    }
}