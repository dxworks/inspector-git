package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.dto.IntegrationDTO

interface IntegrationService {
    fun getByPlatform(platform: String): Any
    fun create(integrationDTO: IntegrationDTO)
    fun deleteByNameAndPlatform(name: String, platform: String)
    fun getAll(): List<IntegrationDTO>
    fun findByNameAndPlatform(integrationName: String, platform: String): IntegrationDTO
}