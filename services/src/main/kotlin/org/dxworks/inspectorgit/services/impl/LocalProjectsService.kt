package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.dto.localProjects.LocalProjectDTO
import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.iglog.readers.IGLogReader
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.appFolderPath
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths

@Service
class LocalProjectsService(private val loadedSystem: LoadedSystem) {
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

        val projects = allIglogs.map {
            val gitLogDTO = IGLogReader().read(it.inputStream())
            ProjectTransformer(gitLogDTO, it.toPath().fileName.toString()).transform()
        }

        iglogs.forEach { it.copyTo(systemFolder.resolve(it.toPath().fileName.toString())) }

        loadedSystem.set(localProjectDTO.id, localProjectDTO.name, projects)


    }
}