package org.dxworks.inspectorgit.persistence.repositories

import org.dxworks.inspectorgit.persistence.entities.ProjectEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : CrudRepository<ProjectEntity, UUID> {
    fun findByPath(path: String): ProjectEntity
    fun deleteByPath(path: String)
}