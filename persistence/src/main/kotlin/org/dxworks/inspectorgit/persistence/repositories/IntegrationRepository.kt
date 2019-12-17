package org.dxworks.inspectorgit.persistence.repositories

import org.dxworks.inspectorgit.persistence.entities.IntegrationEntity
import org.springframework.data.repository.CrudRepository
import java.util.*

interface IntegrationRepository : CrudRepository<IntegrationEntity, UUID> {
    fun deleteByNameAndPlatform(name: String, platform: String)
    fun getAllByPlatform(platform: String): List<IntegrationEntity>
    fun getByNameAndPlatform(name: String, platform: String): IntegrationEntity
}