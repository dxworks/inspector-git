package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.IntegrationDTO
import org.dxworks.inspectorgit.persistence.repositories.IntegrationRepository
import org.dxworks.inspectorgit.services.IntegrationService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class IntegrationServiceImpl(private val integrationRepository: IntegrationRepository) : IntegrationService {
    override fun getByPlatform(platform: String): List<IntegrationDTO> {
        return integrationRepository.getAllByPlatform(platform).map { IntegrationDTO.fromEntity(it) }
    }

    override fun create(integrationDTO: IntegrationDTO) {
        integrationRepository.save(integrationDTO.toEntity())
    }

    @Transactional
    override fun deleteByNameAndPlatform(name: String, platform: String) {
        integrationRepository.deleteByNameAndPlatform(name, platform)
    }
}