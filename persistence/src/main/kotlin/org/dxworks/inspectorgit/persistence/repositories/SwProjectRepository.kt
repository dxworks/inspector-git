package org.dxworks.inspectorgit.persistence.repositories

import org.dxworks.inspectorgit.persistence.entities.SwProjectEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SwProjectRepository : CrudRepository<SwProjectEntity, UUID> {
    fun findByPath(path: String): SwProjectEntity
    fun deleteByPath(path: String)
    fun existsByPath(path: String): Boolean
}