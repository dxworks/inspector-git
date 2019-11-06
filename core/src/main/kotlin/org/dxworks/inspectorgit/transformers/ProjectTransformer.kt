package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.client.dto.ProjectDTO
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

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
