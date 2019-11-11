package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.persistence.dto.ProjectDTO
import org.slf4j.LoggerFactory

class ProjectTransformer(private val projectDTO: ProjectDTO) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)
    }

    fun transform(): Project {
        val projectId = projectDTO.name
        val project = Project(projectId)
        LOG.info("Creating project $projectId")
        projectDTO.gitLogDTO.commits.forEach {
            CommitTransformer(it, project).addToProject()
        }
        LOG.info("Done creating project $projectId")
        return project
    }
}
