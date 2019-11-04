package org.dxworks.inspectorgit.transformers

import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

@Slf4j
class ProjectTransformer(private val projectDTO: ProjectDTO, private val projectId: String) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)
    }

    fun transform(): Project {
        val project = Project(projectId)
        LOG.info("Creating project $projectId")
        projectDTO.commits.forEach {
            CommitTransformer(it, project).addToProject()
        }
        LOG.info("Done creating project $projectId")
        return project
    }
}
