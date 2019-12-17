package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.SystemDTO
import org.dxworks.inspectorgit.persistence.entities.SystemEntity
import org.dxworks.inspectorgit.persistence.repositories.SwProjectRepository
import org.dxworks.inspectorgit.persistence.repositories.SystemRepository
import org.dxworks.inspectorgit.services.SystemService
import org.springframework.stereotype.Service

@Service
class SystemServiceImpl(private val systemRepository: SystemRepository,
                        private val swProjectRepository: SwProjectRepository) : SystemService {
    override fun create(systemDTO: SystemDTO) {
        val systemEntity = SystemEntity()
        systemEntity.name = systemDTO.name
        systemEntity.systemId = systemDTO.systemId
        systemEntity.swProjects = systemDTO.projects?.map { swProjectRepository.findByPath(it.path!!) } ?: emptyList()
        systemRepository.save(systemEntity)
    }
}