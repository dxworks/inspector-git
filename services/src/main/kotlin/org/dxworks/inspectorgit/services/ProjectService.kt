package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.dto.SwProjectDTO
import org.dxworks.inspectorgit.persistence.entities.SwProjectEntity

interface ProjectService {
    fun findAllSwProjects(): Collection<SwProjectDTO>
    fun import(swProjectDTO: SwProjectDTO, username: String, password: String): SwProjectEntity
    fun delete(path: String)
    fun findAllSwProjectsWithoutLog(): List<SwProjectDTO>
    fun existsByPath(path: String): Boolean
}