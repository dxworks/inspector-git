package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.persistence.dto.ProjectDTO
import org.dxworks.inspectorgit.persistence.entities.ProjectEntity
import org.dxworks.inspectorgit.persistence.repositories.ProjectRepository
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(private val projectRepository: ProjectRepository,
                         private val gitRepositoryService: GitRepositoryService) : ProjectService {
    override fun create(projectDTO: ProjectDTO, username: String, password: String) {
        gitRepositoryService.clone(projectDTO.repositoryHttpUrl, projectDTO.path, projectDTO.branch, username, password)
        projectDTO.gitLogDTO = gitRepositoryService.getGitLog(projectDTO.path)
        projectRepository.save(ProjectEntity.fromDto(projectDTO))
    }

    override fun delete(path: String) {
        gitRepositoryService.delete(path)
        projectRepository.deleteByPath(path)
    }

    override fun getProjectsWithoutLog(): List<ProjectDTO> {
        return projectRepository.findAll().map {
            ProjectDTO(it.name, it.path, it.branch, it.integrationPath, it.repositoryHttpUrl, it.webUrl, it.pullRequestsEnabled)
        }
    }

    override fun findAllProjects() = projectRepository.findAll().map { it.toDto() }
}