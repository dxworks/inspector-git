package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.persistence.dto.ProjectDTO

interface ProjectService {
    fun findAllProjects(): Collection<ProjectDTO>
    fun create(projectDTO: ProjectDTO, username: String, password: String)
    fun delete(path: String)
    fun getProjectsWithoutLog(): List<ProjectDTO>
}