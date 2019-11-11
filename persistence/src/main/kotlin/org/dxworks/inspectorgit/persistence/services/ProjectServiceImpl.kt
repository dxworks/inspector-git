package org.dxworks.inspectorgit.persistence.services

import org.dxworks.inspectorgit.persistence.dto.ProjectDTO
import org.dxworks.inspectorgit.persistence.entities.ProjectEntity
import org.dxworks.inspectorgit.persistence.repositories.ProjectRepository
import org.springframework.stereotype.Service

@Service
class ProjectServiceImpl(private val projectRepository: ProjectRepository) : ProjectService {
    override fun findAllProjects() = projectRepository.findAll().map { it.toDto() }
    override fun findProjectByName(name: String) = projectRepository.findByName(name).toDto()
    override fun saveProject(projectDTO: ProjectDTO) = projectRepository.save(ProjectEntity.fromDto(projectDTO)).toDto()
}