package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.SwProjectDTO
import org.dxworks.inspectorgit.persistence.entities.SwProjectEntity
import org.dxworks.inspectorgit.persistence.repositories.SwProjectRepository
import org.dxworks.inspectorgit.services.GitRepositoryService
import org.dxworks.inspectorgit.services.ProjectService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class ProjectServiceImpl(private val swProjectRepository: SwProjectRepository) : ProjectService {

    private val gitRepositoryService: GitRepositoryService = GitRepositoryService()

    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectService::class.java)
    }

    override fun import(swProjectDTO: SwProjectDTO, username: String, password: String): SwProjectEntity {
        LOG.info("Importing ${swProjectDTO.name}")
        gitRepositoryService.clone(swProjectDTO.repositoryHttpUrl!!,
                swProjectDTO.path!!,
                swProjectDTO.branch ?: "master",
                username, password)

        swProjectDTO.gitLogDTO = gitRepositoryService.getGitLog(swProjectDTO.path!!)
        val entity = swProjectDTO.toEntity()
        entity.imported = true
        LOG.info("Imported ${swProjectDTO.name}")
        return swProjectRepository.save(entity)
    }

    @Transactional
    override fun delete(path: String) {
        gitRepositoryService.delete(path)
        swProjectRepository.deleteByPath(path)
    }

    override fun findAllSwProjectsWithoutLog(): List<SwProjectDTO> {
        return swProjectRepository.findAll().map { SwProjectDTO.fromEntity(it, includeLogs = false) }
    }

    override fun findAllSwProjects() = swProjectRepository.findAll().map { SwProjectDTO.fromEntity(it) }

    override fun existsByPath(path: String): Boolean {
        return swProjectRepository.existsByPath(path)
    }
}