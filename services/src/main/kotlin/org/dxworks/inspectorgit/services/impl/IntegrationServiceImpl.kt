package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.persistence.repositories.IntegrationRepository
import org.dxworks.inspectorgit.services.IntegrationService
import org.dxworks.inspectorgit.services.dto.IntegrationDTO
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class IntegrationServiceImpl(private val integrationRepository: IntegrationRepository) : IntegrationService {
    override fun getByPlatform(platform: String): List<IntegrationDTO> {
        return integrationRepository.getAllByPlatform(platform).map { IntegrationDTO.fromEntity(it) }
    }

    override fun findByNameAndPlatform(integrationName: String, platform: String): IntegrationDTO {
        return IntegrationDTO.fromEntity(integrationRepository.getByNameAndPlatform(integrationName, platform))
    }

    override fun getAll(): List<IntegrationDTO> {
        return integrationRepository.findAll().map { IntegrationDTO.fromEntity(it) }
    }

    override fun create(integrationDTO: IntegrationDTO) {
        integrationRepository.save(integrationDTO.toEntity())
    }

    @Transactional
    override fun deleteByNameAndPlatform(name: String, platform: String) {
        integrationRepository.deleteByNameAndPlatform(name, platform)
    }
}