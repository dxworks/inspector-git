package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.SystemDTO
import org.dxworks.inspectorgit.persistence.entities.SystemEntity
import org.dxworks.inspectorgit.persistence.repositories.SwProjectRepository
import org.dxworks.inspectorgit.persistence.repositories.SystemRepository
import org.dxworks.inspectorgit.services.SystemService
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class SystemServiceImpl(private val systemRepository: SystemRepository,
                        private val gitlabIntegrationService: GitlabIntegrationService,
                        private val swProjectRepository: SwProjectRepository) : SystemService {
    override fun create(systemDTO: SystemDTO) {
        val projectEntities = systemDTO.projects?.filter { it.platform == "gitlab" }?.let { gitlabIntegrationService.import(it) }

        val systemEntity = SystemEntity()
        systemEntity.name = systemDTO.name
        systemEntity.systemId = systemDTO.systemId
        systemEntity.swProjects = (systemDTO.projects?.mapNotNull { swProjectRepository.findByPath("${it.platform}/${it.path}") }
                ?: emptyList()).toList() + (projectEntities ?: emptyList())
        systemRepository.save(systemEntity)
    }

//    override fun analyze(systemId: String): Map<String, List<WorkAnalyzerNumbersDTO>>? {
//        val systemEntity = this.systemRepository.findBySystemId(systemId)
//        this.configurationService.configureAll()
//        val results = systemEntity.swProjects?.parallelStream()
//                ?.map { SwProjectDTO.fromEntity(it) }
//                ?.map { ProjectTransformer(it.gitLogDTO!!, it.name!!).transform() }
//                ?.map { it.name to workAnalyzer.analyze(it).map { result -> WorkAnalyzerNumbersDTO.get(result) } }
//                ?.toList()
//        val map = results?.toMap()
//        FileSystemUtils.writeResults(systemEntity.systemId!!, map)
//        return map
//    }

    @Transactional
    override fun delete(systemId: String) {
        this.systemRepository.deleteBySystemId(systemId)
    }

    override fun findAll(): List<SystemDTO> {
        return this.systemRepository.findAll().map { SystemDTO.fromEntity(it, includeProjects = false) }
    }
}