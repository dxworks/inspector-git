package org.dxworks.inspectorgit.remote

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.model.ComposedProject
import org.dxworks.inspectorgit.remote.dtos.RemoteInfoDTO
import org.dxworks.inspectorgit.transformers.RemoteGitTransformer
import java.nio.file.Path

class RemoteInfoImporter {
    fun import(path: Path, composedProject: ComposedProject): ComposedProject {
        val mapper = jacksonObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val importDTO = mapper.readValue<RemoteInfoDTO>(path.toFile())
        RemoteGitTransformer(composedProject, importDTO).transform()
        return composedProject
    }
}