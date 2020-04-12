package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.localProjects.LocalProjectDTO
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

@Service
class LocalProjectsService(private val loadedSystem: LoadedSystem,
                           private val localSystemRepository: LocalSystemRepository) {
    fun create(localProjectDTO: LocalProjectDTO) {
        val systemFolder = appFolderPath.resolve(localProjectDTO.id).toFile()
        if (systemFolder.exists())
            throw FileAlreadyExistsException(systemFolder, reason = "Project with id ${localProjectDTO.id} already exists")

        systemFolder.mkdir()

        val (repos, iglogs) = localProjectDTO.sources
                .map { Paths.get(it).toFile() }
                .onEach { if (!it.exists()) throw FileNotFoundException("${it.absolutePath} is not a file or folder.") }
                .partition { it.isDirectory }

        repos.forEach { MetadataExtractionManager(it.toPath(), systemFolder.toPath()).extract() }

        val allIglogs = iglogs + (systemFolder.list()?.map { systemFolder.resolve(it) } ?: emptyList<File>())

        val projects = transformProjects(allIglogs)

        iglogs.forEach { it.copyTo(systemFolder.resolve(it.name)) }

        loadedSystem.set(localProjectDTO.id, localProjectDTO.name, projects)

        localSystemRepository.save(LocalSystemEntity(systemId = localProjectDTO.id,
                name = localProjectDTO.name))
    }

    fun load(id: String) {
        val systemFolder = appFolderPath.resolve(id).toFile()
        if (!systemFolder.exists()) {
            localSystemRepository.deleteBySystemId(id)
            throw FileNotFoundException("Project with id $id does not exist")
        }
        val iglogs = systemFolder.list()?.map { systemFolder.resolve(it) } ?: emptyList()

        val name = localSystemRepository.findBySystemId(id).name

        val projects = transformProjects(iglogs)

        loadedSystem.set(id, name, projects)
    }

    private fun transformProjects(allIglogs: List<File>): List<Project> {
        return allIglogs.parallelStream().map {
            val gitLogDTO = IGLogReader().read(it.inputStream())
            ProjectTransformer(gitLogDTO, it.nameWithoutExtension).transform()
        }.collect(Collectors.toList())
    }
}