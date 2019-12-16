package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.dto.GitlabCredentialsDTO
import org.dxworks.inspectorgit.dto.ImportGitlabProjectsDTO
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.services.ProjectService
import org.dxworks.inspectorgit.web.apiPath
import org.dxworks.inspectorgit.web.dto.GitlabSimpleProjectResponseDTO
import org.dxworks.inspectorgit.web.services.GitlabIntegrationService
import org.springframework.web.bind.annotation.*

//@CrossOrigin(origins = ["http://localhost:4200"], maxAge = 3600)
@RestController
@RequestMapping("$apiPath/gitlab")
class GitLabIntegrationController(private val gitlabIntegrationService: GitlabIntegrationService,
                                  private val projectService: ProjectService) {

    @PutMapping("importProjects")
    fun importProjects(@RequestBody importProjectsDTO: ImportGitlabProjectsDTO) {
        this.gitlabIntegrationService.import(importProjectsDTO);
    }

    @PutMapping("listProjects")
    fun listProjects(@RequestBody gitlabCredentialsDTO: GitlabCredentialsDTO): List<GitlabSimpleProjectResponseDTO>? {
        return gitlabIntegrationService.listRepositories(gitlabCredentialsDTO)
    }

    @DeleteMapping("deleteProject")
    fun deleteProject(@RequestParam(required = true) path: String) {
        projectService.delete(path)
    }

    @GetMapping("projects")
    fun getProjects(): List<ProjectDTO> {
        return this.projectService.getProjectsWithoutLog()
    }
}