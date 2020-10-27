package org.dxworks.inspectorgit.services

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.factories.ProjectFactories
import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.iglog.readers.IGLogReader
import org.dxworks.inspectorgit.jira.dtos.IssueTrackerImportDTO
import org.dxworks.inspectorgit.persistence.entities.LocalSystemEntity
import org.dxworks.inspectorgit.persistence.repositories.LocalSystemRepository
import org.dxworks.inspectorgit.remote.dtos.RemoteInfoDTO
import org.dxworks.inspectorgit.services.chronos.ChronosSettingsService
import org.dxworks.inspectorgit.services.dto.LocalSystemDTO
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getSystemFolder
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Paths
import java.util.stream.Collectors
import javax.transaction.Transactional

@Service
class LocalSystemsService(private val loadedSystem: LoadedSystem,
                          private val localSystemRepository: LocalSystemRepository,
                          private val chronosSettingsService: ChronosSettingsService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(LocalSystemsService::class.java)
    }

    private val mapper = jacksonObjectMapper()

    init {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun create(localSystemDTO: LocalSystemDTO) {
        val systemFolder = getSystemFolder(localSystemDTO.id)
        if (systemFolder.exists())
            throw FileAlreadyExistsException(systemFolder, reason = "Project with id ${localSystemDTO.id} already exists")

        systemFolder.mkdir()

        try {

            val (repos, iglogs) = mapToFilesOrThrow(localSystemDTO.sources)
                    .partition { it.isDirectory }

            repos.forEach { MetadataExtractionManager(it.toPath(), systemFolder.toPath()).extract() }

            localSystemDTO.sources = (iglogs + (systemFolder.list()?.map { systemFolder.resolve(it) }
                    ?: emptyList())).map { it.absolutePath }

            val issueFiles = mapToFilesOrThrow(localSystemDTO.issues)
            val remoteFiles = mapToFilesOrThrow(localSystemDTO.remotes)
            loadSystem(localSystemDTO)

            (iglogs + issueFiles + remoteFiles).forEach { it.copyTo(systemFolder.resolve(it.name)) }

            localSystemRepository.save(LocalSystemEntity(localSystemDTO.id,
                    localSystemDTO.name, (iglogs + repos).map { it.nameWithoutExtension + ".iglog" }, issueFiles.map { it.name }, remoteFiles.map { it.name }))
        } catch (e: Exception) {
            LOG.error("Project not loaded successfully!", e)
            systemFolder.deleteRecursively()
            throw e
        }
    }

    private fun loadSystem(localSystemDTO: LocalSystemDTO) {
        val allIglogs = mapToFilesOrThrow(getSystemFiles(localSystemDTO.id, localSystemDTO.sources))
        val issueFiles = mapToFilesOrThrow(getSystemFiles(localSystemDTO.id, localSystemDTO.issues))
        val remoteFiles = mapToFilesOrThrow(getSystemFiles(localSystemDTO.id, localSystemDTO.remotes))

        val gitLogsAndNames = allIglogs.parallelStream().map { Pair(IGLogReader().read(it.inputStream()), it.nameWithoutExtension) }.collect(Collectors.toList())
        val issueTrackerImportDTOsAndNames = issueFiles.map {
            Pair(mapper.readValue<IssueTrackerImportDTO>(it.readText().replace("[^\\x20-\\x7e]", "")), it.nameWithoutExtension)
        }
        val remoteImportDTOsAndNames = remoteFiles.map {
            Pair(mapper.readValue<RemoteInfoDTO>(it.readText().replace("[^\\x20-\\x7e]", "")), it.nameWithoutExtension)
        }


        val projects = (gitLogsAndNames + issueTrackerImportDTOsAndNames + remoteImportDTOsAndNames)
                .map { ProjectFactories.create(it.first, it.second) }

        loadedSystem.set(localSystemDTO.id, localSystemDTO.name, projects)
        applyChronosMergesIfPossible()
    }

    private fun applyChronosMergesIfPossible() {
        try {
            chronosSettingsService.applyMerges()
        } catch (e: Exception) {
            LOG.warn("Could not apply Chronos merges: ${e.message}")
        }
    }

    private fun getSystemFiles(systemId: String, fileNames: List<String>): List<String> {
        val systemFolder = getSystemFolder(systemId)
        return fileNames.map { systemFolder.resolve(it).toString() }
    }

    private fun mapToFilesOrThrow(files: List<String>): List<File> {
        return files.map { Paths.get(it).toFile() }
                .onEach { if (!it.exists()) throw FileNotFoundException("${it.absolutePath} is not a file or folder.") }
    }

    fun load(id: String) {
        if (loadedSystem.isSet && loadedSystem.id == id)
            return

        val systemFolder = getSystemFolder(id)
        if (!systemFolder.exists()) {
            localSystemRepository.deleteBySystemId(id)
            throw FileNotFoundException("Project with id $id does not exist")
        }

        loadSystem(localSystemDTO(localSystemRepository.findBySystemId(id)))
    }

    fun list(): List<LocalSystemDTO> {
        return localSystemRepository.findAll().map { localSystemDTO(it) }
    }

    private fun localSystemDTO(it: LocalSystemEntity) =
            LocalSystemDTO(it.systemId, it.name, it.sources, it.issues, it.remotes)

    @Transactional
    fun delete(id: String) {
        getSystemFolder(id).deleteRecursively()
        localSystemRepository.deleteBySystemId(id)
    }
}
