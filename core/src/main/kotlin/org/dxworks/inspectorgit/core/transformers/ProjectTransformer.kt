package org.dxworks.inspectorgit.core.transformers

import org.dxworks.inspectorgit.core.ChangeFactory
import org.dxworks.inspectorgit.core.SimpleChangeFactory
import org.dxworks.inspectorgit.core.model.Project
import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
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
        getSortedCommits(gitLogDTO.commits)
        while (commits.size > 0) {
            commits.removeAt(0).let { CommitTransformer.addToProject(it, project, changeFactory) }
        }
        LOG.info("Done creating project $name")
        return project
    }

    private fun getSortedCommits(commits: List<CommitDTO>): MutableList<CommitDTO> {
        val referencedCommits = commits.flatMap { it.parentIds }.toSet()
        val commitsMap = commits.map { Pair(it.id, it) }.toMap()
        val lastCommitKey = (commitsMap.keys - referencedCommits).first()

        val lastCommit = commitsMap[lastCommitKey]!!
        val orderedCommitKeys = LinkedHashSet<String>()
        var commits = listOf(lastCommit)

        while (commits.any { it.parentIds.isNotEmpty() }) {
            orderedCommitKeys.addAll(commits.map { it.id })
            commits = commits.flatMap { it.parentIds }.distinct().mapNotNull { commitsMap[it] }
        }
        return orderedCommitKeys.mapNotNull { commitsMap[it] }.toMutableList()
    }
}
