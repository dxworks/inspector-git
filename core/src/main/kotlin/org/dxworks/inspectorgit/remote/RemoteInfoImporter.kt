package org.dxworks.inspectorgit.remote

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.remote.dtos.RemoteInfoDTO
import org.dxworks.inspectorgit.transformers.PRTransformer
import java.nio.file.Path

class RemoteInfoImporter {
    fun import(path: Path, project: Project): Project {
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val importDTO = mapper.readValue<RemoteInfoDTO>(path.toFile())
        PRTransformer(project, importDTO).addToProject()
        return project
    }
}