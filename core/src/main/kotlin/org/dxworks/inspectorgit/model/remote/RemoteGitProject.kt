package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.remote.PullRequestRegistry
import org.dxworks.inspectorgit.registries.remote.RemoteRepoRegistry
import org.dxworks.inspectorgit.registries.remote.SimpleBranchRegistry
import org.dxworks.inspectorgit.transformers.getRegexWithWordBoundaryGroups

class RemoteGitProject(override val name: String) : Project {

    val accountRegistry = AccountRegistry()
    val pullRequestRegistry = PullRequestRegistry()
    val repoRegistry = RemoteRepoRegistry()
    val simpleBranchRegistry = SimpleBranchRegistry()

    override fun link(projects: List<Project>) {
        val issueTrackerProjects = projects.filterIsInstance<IssueTrackerProject>()
        val gitProjects = projects.filterIsInstance<GitProject>()
        pullRequestRegistry.all.forEach {
            it.commits = getCommits(gitProjects, it.commitIds)
            it.issue = getLinkedIssue(issueTrackerProjects, "${it.title} ${it.head.ref}")
            it.issue?.let { issue -> issue.pullRequests += it }
            addPullRequestToCommits(it)
        }
    }

    private fun getLinkedIssue(projects: List<IssueTrackerProject>, content: String) =
            projects.map {
                it.issueRegistry.allDetailedIssues
                        .find { issue -> getRegexWithWordBoundaryGroups(issue.id).containsMatchIn(content) }
            }.firstOrNull()

    private fun getCommits(projects: List<GitProject>, commits: List<String>): List<Commit> = commits.mapNotNull { getCommit(projects, it) }

    private fun getCommit(projects: List<GitProject>, id: String) = projects.map { it.commitRegistry.getById(id) }.firstOrNull()

    private fun addPullRequestToCommits(pullRequest: PullRequest) {
        (pullRequest.commits + pullRequest.base.commit + pullRequest.head.commit)
                .filterNotNull()
                .distinct()
                .forEach { it.pullRequests += pullRequest }
    }

    override fun unlink(projects: List<Project>) {
        TODO("Not yet implemented")
    }

    override var system: System? = null
}