package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.persistence.entities.SystemEntity
import org.dxworks.inspectorgit.persistence.repositories.SwProjectRepository
import org.dxworks.inspectorgit.persistence.repositories.SystemRepository
import org.dxworks.inspectorgit.services.SystemService
import org.dxworks.inspectorgit.services.dto.SystemDTO
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class SystemServiceImpl(private val systemRepository: SystemRepository,
                        private val gitlabIntegrationService: GitlabIntegrationService,
                        private val swProjectRepository: SwProjectRepository
) : SystemService {
    override fun create(systemDTO: SystemDTO) {
        val projectEntities = systemDTO.projects?.filter { it.platform == "gitlab" }?.let { gitlabIntegrationService.import(it) }

        val systemEntity = SystemEntity()
        systemEntity.name = systemDTO.name
        systemEntity.systemId = systemDTO.systemId
        systemEntity.swProjects = (systemDTO.projects?.mapNotNull { swProjectRepository.findByPath("${it.platform}/${it.path}") }
                ?: emptyList()).toList() + (projectEntities ?: emptyList())
        systemRepository.save(systemEntity)
    }

    @Transactional
    override fun delete(systemId: String) {
        this.systemRepository.deleteBySystemId(systemId)
    }

    override fun findAll(): List<SystemDTO> {
        return this.systemRepository.findAll().map { SystemDTO.fromEntity(it, includeProjects = false) }
    }
}