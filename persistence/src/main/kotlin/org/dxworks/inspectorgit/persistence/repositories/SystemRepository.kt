package org.dxworks.inspectorgit.persistence.repositories

import org.dxworks.inspectorgit.persistence.entities.SystemEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface SystemRepository : CrudRepository<SystemEntity, UUID> {
    fun findBySystemId(systemId: String): SystemEntity
    fun deleteBySystemId(systemId: String)
}