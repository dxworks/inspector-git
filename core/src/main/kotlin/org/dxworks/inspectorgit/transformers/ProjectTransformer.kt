package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

class ProjectTransformer(private val gitLogDTO: GitLogDTO, private val name: String) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)
    }

    fun transform(): Project {
        val project = Project(name)
        LOG.info("Creating project $name")
        gitLogDTO.commits.forEach {
            CommitTransformer(it, project).addToProject()
        }
        LOG.info("Done creating project $name")
        return project
    }
}
