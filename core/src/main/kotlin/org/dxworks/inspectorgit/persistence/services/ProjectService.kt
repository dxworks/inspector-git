package org.dxworks.inspectorgit.persistence.services

import org.dxworks.inspectorgit.persistence.dto.ProjectDTO

interface ProjectService {
    fun findAllProjects(): Collection<ProjectDTO>
    fun findProjectByName(name: String): ProjectDTO
    fun saveProject(projectDTO: ProjectDTO): ProjectDTO
}