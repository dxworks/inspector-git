package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.ImportGitlabProjectsDTO
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.persistence.repositories.ProjectRepository
import org.dxworks.inspectorgit.services.GitRepositoryService
import org.dxworks.inspectorgit.services.ProjectService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ProjectServiceImpl(private val projectRepository: ProjectRepository,
                         private val gitRepositoryService: GitRepositoryService) : ProjectService {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectService::class.java)
    }

    override fun import(projectDTO: ProjectDTO, username: String, password: String) {
        LOG.info("Importing ${projectDTO.name}")
        gitRepositoryService.clone(projectDTO.repositoryHttpUrl!!, projectDTO.path!!, projectDTO.branch, username, password)
        projectDTO.gitLogDTO = gitRepositoryService.getGitLog(projectDTO.path!!)
        projectRepository.save(projectDTO.toEntity())
        LOG.info("Imported ${projectDTO.name}")
    }

    @Transactional
    override fun delete(path: String) {
        gitRepositoryService.delete(path)
        projectRepository.deleteByPath(path)
    }

    override fun getProjectsWithoutLog(): List<ProjectDTO> {
        return projectRepository.findAll().map {
            ProjectDTO(it.name, it.path, it.branch, it.integrationPath, it.repositoryHttpUrl, it.webUrl, it.pullRequestsEnabled)
        }
    }

    override fun findAllProjects() = projectRepository.findAll().map { ProjectDTO.fromEntity(it) }
}