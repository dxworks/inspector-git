package org.dxworks.inspectorgit.core.transformers

import org.dxworks.inspectorgit.core.ChangeFactory
import org.dxworks.inspectorgit.core.SimpleChangeFactory
import org.dxworks.inspectorgit.core.model.Project
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.slf4j.LoggerFactory

class ProjectTransformer(private val gitLogDTO: GitLogDTO, private val name: String, private val changeFactory: ChangeFactory = SimpleChangeFactory(), private var project: Project? = null) {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)
    }

    fun transform(): Project {
        val project = project ?: Project(name)
        LOG.info("Creating project $name")
        val commits = gitLogDTO.commits.toMutableList()
        while (commits.size > 0) {
            commits.removeAt(0).let { CommitTransformer.addToProject(it, project, changeFactory) }
        }
        LOG.info("Done creating project $name")
        return project
    }
}
