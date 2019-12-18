package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.dto.SwProjectDTO
import org.dxworks.inspectorgit.services.ProjectService
import org.dxworks.inspectorgit.web.apiPath
import org.dxworks.inspectorgit.services.impl.GitlabIntegrationService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$apiPath/gitlab")
class GitLabIntegrationController(private val gitlabIntegrationService: GitlabIntegrationService,
                                  private val projectService: ProjectService) {

    @PutMapping("importProjects")
    fun importProjects(@RequestBody projectsDTO: List<SwProjectDTO>) {
        this.gitlabIntegrationService.import(projectsDTO);
    }

    @GetMapping("listProjects")
    fun listProjects(@RequestParam(required = true) integrationName: String): List<SwProjectDTO>? {
        return gitlabIntegrationService.listProjects(integrationName)
    }

    @DeleteMapping("deleteProject")
    fun deleteProject(@RequestParam(required = true) path: String) {
        projectService.delete(path)
    }

    @GetMapping("projects")
    fun getProjects(): List<SwProjectDTO> {
        return this.projectService.findAllSwProjectsWithoutLog()
    }
}