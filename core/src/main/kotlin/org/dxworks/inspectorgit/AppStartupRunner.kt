package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.client.dto.GitLogDTO
import org.dxworks.inspectorgit.persistence.dto.ProjectDTO
import org.dxworks.inspectorgit.persistence.services.ProjectService
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.JsonUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class AppStartupRunner(private val projectService: ProjectService) : CommandLineRunner {
    companion object {
        private val LOG = LoggerFactory.getLogger(AppStartupRunner::class.java)
    }

    private val projectName = "kafka"

    override fun run(vararg args: String) {
        val gitLogDTO = JsonUtils.jsonFromFile(FileSystemUtils.getDtoFilePathFor(projectName, "trunk"), GitLogDTO::class.java)
        projectService.saveProject(ProjectDTO(projectName, gitLogDTO))
//        val project = ProjectTransformer(gitLogDTO, projectName).transform()
//        println(project.hashCode())
    }
}