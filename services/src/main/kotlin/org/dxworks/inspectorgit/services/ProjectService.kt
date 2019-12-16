package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.dto.ProjectDTO

interface ProjectService {
    fun findAllProjects(): Collection<ProjectDTO>
    fun import(projectDTO: ProjectDTO, username: String, password: String)
    fun delete(path: String)
    fun getProjectsWithoutLog(): List<ProjectDTO>
}