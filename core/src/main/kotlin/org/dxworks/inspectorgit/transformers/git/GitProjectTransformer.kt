package org.dxworks.inspectorgit.transformers.git

import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.model.git.GitProject
import org.slf4j.LoggerFactory

class GitProjectTransformer(private val gitLogDTO: GitLogDTO, private val name: String = "Project", private val changeFactory: ChangeFactory = SimpleChangeFactory()) {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitProjectTransformer::class.java)
    }

    fun transform(): GitProject {
        val project = GitProject(name)
        LOG.info("Creating project $name")
        gitLogDTO.commits.forEach {
            CommitTransformer(it, project, changeFactory).addToProject()
        }
        LOG.info("Done creating project $name")
        return project
    }
}
