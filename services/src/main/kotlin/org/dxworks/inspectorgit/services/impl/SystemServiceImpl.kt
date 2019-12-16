package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.SystemDTO
import org.dxworks.inspectorgit.persistence.repositories.ProjectRepository
import org.dxworks.inspectorgit.persistence.repositories.SystemRepository
import org.dxworks.inspectorgit.services.SystemService
import org.springframework.stereotype.Service

@Service
class SystemServiceImpl(private val systemRepository: SystemRepository,
                        private val projectRepository: ProjectRepository) : SystemService {
    override fun create(systemDTO: SystemDTO) {
//        systemRepository.save(SystemEntity(systemDTO.name, systemDTO.projects.map { projectRepository.findByPath(it.path!!) }))
    }
}