package org.dxworks.inspectorgit.jira

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.jira.dtos.IssueTrackerImportDTO
import org.dxworks.inspectorgit.model.ComposedProject
import org.dxworks.inspectorgit.transformers.IssueTrackerTransformer
import java.nio.file.Path

class TaskImporter {
    fun import(path: Path, composedProject: ComposedProject = ComposedProject(path.normalize().fileName.toString())): ComposedProject {
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val importDTO = mapper.readValue<IssueTrackerImportDTO>(path.toFile())
        IssueTrackerTransformer(importDTO.issueStatuses, importDTO.issueTypes, importDTO.users, importDTO.issues, "name").transform()
        return composedProject
    }
}