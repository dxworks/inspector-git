package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.dto.SwProjectDTO

interface ProjectService {
    fun findAllSwProjects(): Collection<SwProjectDTO>
    fun import(swProjectDTO: SwProjectDTO, username: String, password: String)
    fun delete(path: String)
    fun findAllSwProjectsWithoutLog(): List<SwProjectDTO>
}