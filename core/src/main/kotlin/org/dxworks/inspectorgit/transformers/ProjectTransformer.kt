package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.client.dto.GitLogDTO
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

class ProjectTransformer(private val gitLogDTO: GitLogDTO, private val projectId: String) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)
    }

    fun transform(): Project {
        val project = Project(projectId)
        LOG.info("Creating project $projectId")
        gitLogDTO.commits.forEach {
            CommitTransformer(it, project).addToProject()
        }
        LOG.info("Done creating project $projectId")
        return project
    }
}
