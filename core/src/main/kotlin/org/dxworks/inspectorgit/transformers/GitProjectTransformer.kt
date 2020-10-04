package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.transformers.git.ChangeFactory
import org.dxworks.inspectorgit.transformers.git.CommitTransformer
import org.dxworks.inspectorgit.transformers.git.SimpleChangeFactory
import org.slf4j.LoggerFactory

class GitProjectTransformer(private val gitLogDTO: GitLogDTO, private val name: String = "Project", private val changeFactory: ChangeFactory = SimpleChangeFactory()) {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitProjectTransformer::class.java)
        private var branchId: Long = 0
    }

    fun transform(): GitProject {
        val project = GitProject(name)
        LOG.info("Creating project $name")
        gitLogDTO.commits.forEach {
            CommitTransformer.addToProject(it, project, changeFactory)
        }
        computeBranchIds(project.commitRegistry.all.first())

        LOG.info("Done creating project $name")
        return project
    }

    private fun computeBranchIds(commit: Commit) {
        val parents = commit.parents

        if (commit.isMergeCommit)
            commit.branchId = parents.first().branchId
        else if (parents.isEmpty() || parents.first().isSplitCommit)
            commit.branchId = branchId.apply { inc() }
        else
            commit.branchId = parents.first().branchId
    }
}
