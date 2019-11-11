package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.client.dto.ProjectDTO
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.JsonUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class AppStartupRunner : CommandLineRunner {
    companion object {
        private val LOG = LoggerFactory.getLogger(AppStartupRunner::class.java)
    }

    override fun run(vararg args: String) {
        val projectDTO = JsonUtils.jsonFromFile(FileSystemUtils.getDtoFilePathFor("kafka", "trunk"), ProjectDTO::class.java)
        val project = ProjectTransformer(projectDTO, "kafka").transform()
        println(project.hashCode())
    }
}