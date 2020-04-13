package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.localProjects.LocalSystemDTO
import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.iglog.readers.IGLogReader
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.persistence.entities.LocalSystemEntity
import org.dxworks.inspectorgit.persistence.repositories.LocalSystemRepository
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.appFolderPath
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
class LocalSystemsService(private val loadedSystem: LoadedSystem,
                          private val localSystemRepository: LocalSystemRepository) {
    fun create(localSystemDTO: LocalSystemDTO) {
        val systemFolder = getSystemFolder(localSystemDTO.id)
        if (systemFolder.exists())
            throw FileAlreadyExistsException(systemFolder, reason = "Project with id ${localSystemDTO.id} already exists")

        systemFolder.mkdir()

        try {

            val (repos, iglogs) = localSystemDTO.sources
                    .map { Paths.get(it).toFile() }
                    .onEach { if (!it.exists()) throw FileNotFoundException("${it.absolutePath} is not a file or folder.") }
                    .partition { it.isDirectory }

            repos.forEach { MetadataExtractionManager(it.toPath(), systemFolder.toPath()).extract() }

            val allIglogs = iglogs + (systemFolder.list()?.map { systemFolder.resolve(it) } ?: emptyList<File>())

            val projects = transformProjects(allIglogs)

            iglogs.forEach { it.copyTo(systemFolder.resolve(it.name)) }

            loadedSystem.set(localSystemDTO.id, localSystemDTO.name, projects)

            localSystemRepository.save(LocalSystemEntity(localSystemDTO.id,
                    localSystemDTO.name, projects.map { it.name }))
        } catch (e: Exception) {
            systemFolder.deleteRecursively()
            throw e
        }
    }

    fun load(id: String) {
        if (loadedSystem.isSet && loadedSystem.id == id)
            return

        val systemFolder = getSystemFolder(id)
        if (!systemFolder.exists()) {
            localSystemRepository.deleteBySystemId(id)
            throw FileNotFoundException("Project with id $id does not exist")
        }
        val iglogs = systemFolder.list()?.map { systemFolder.resolve(it) } ?: emptyList()

        val name = localSystemRepository.findBySystemId(id).name

        val projects = transformProjects(iglogs)

        loadedSystem.set(id, name, projects)
    }

    fun list(): List<LocalSystemDTO> {
        return localSystemRepository.findAll().map { LocalSystemDTO(it.systemId, it.name, it.sources) }
    }

    @Transactional
    fun delete(id: String) {
        getSystemFolder(id).deleteRecursively()
        localSystemRepository.deleteBySystemId(id)
    }

    private fun getSystemFolder(id: String) = appFolderPath.resolve(id).toFile()

    private fun transformProjects(allIglogs: List<File>): List<Project> {
        return allIglogs.parallelStream().map {
            val gitLogDTO = IGLogReader().read(it.inputStream())
            ProjectTransformer(gitLogDTO, it.nameWithoutExtension).transform()
        }.collect(Collectors.toList())
    }
}