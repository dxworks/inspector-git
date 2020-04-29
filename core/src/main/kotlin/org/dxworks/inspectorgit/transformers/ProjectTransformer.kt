package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.ChangeFactory
import org.dxworks.inspectorgit.SimpleChangeFactory
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.model.Project
import org.slf4j.LoggerFactory

class ProjectTransformer(private val gitLogDTO: GitLogDTO, private val name: String = "Project", private val changeFactory: ChangeFactory = SimpleChangeFactory(), private var project: Project? = null) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)
    }

    fun transform(): Project {
        val project = project ?: Project(name)
        LOG.info("Creating project $name")
        gitLogDTO.commits.forEach {
            CommitTransformer(it, project, changeFactory).addToProject()
        }
        LOG.info("Done creating project $name")
        return project
    }
}
