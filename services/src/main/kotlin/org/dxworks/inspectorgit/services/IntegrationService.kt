package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.dto.IntegrationDTO

interface IntegrationService {
    fun getByPlatform(platform: String): Any
    fun create(integrationDTO: IntegrationDTO)
    fun deleteByNameAndPlatform(name: String, platform: String)
}