package org.dxworks.inspectorgit.persistence.repositories

import org.dxworks.inspectorgit.persistence.entities.LocalSystemEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface LocalSystemRepository : CrudRepository<LocalSystemEntity, UUID> {
    fun deleteBySystemId(id: String)
    fun findBySystemId(id: String): LocalSystemEntity
}