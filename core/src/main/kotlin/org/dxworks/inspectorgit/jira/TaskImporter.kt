package org.dxworks.inspectorgit.jira

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.jira.dtos.TaskImportDTO
import org.dxworks.inspectorgit.model.ComposedProject
import org.dxworks.inspectorgit.transformers.IssueTrackerTransformer
import java.nio.file.Path

class TaskImporter {
    fun import(path: Path, taskPrefixes: List<String> = emptyList(), composedProject: ComposedProject = ComposedProject(path.normalize().fileName.toString())): ComposedProject {
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val importDTO = mapper.readValue<TaskImportDTO>(path.toFile())
        IssueTrackerTransformer(composedProject, importDTO.issueStatuses, importDTO.issueTypes, importDTO.users, importDTO.issues, taskPrefixes).transform()
        return composedProject
    }
}