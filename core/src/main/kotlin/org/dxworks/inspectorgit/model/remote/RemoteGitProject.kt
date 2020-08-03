package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.remote.CommitRemoteInfoRegistry
import org.dxworks.inspectorgit.registries.remote.PullRequestRegistry
import org.dxworks.inspectorgit.registries.remote.RemoteRepoRegistry
import org.dxworks.inspectorgit.registries.remote.SimpleBranchRegistry
import org.dxworks.inspectorgit.transformers.getRegexWithWordBoundaryGroups

class RemoteGitProject(override val name: String) : Project {

    val accountRegistry = AccountRegistry()
    val pullRequestRegistry = PullRequestRegistry()
    val repoRegistry = RemoteRepoRegistry()
    val simpleBranchRegistry = SimpleBranchRegistry()
    val commitRemoteInfoRegistry = CommitRemoteInfoRegistry()

    override fun link(projects: List<Project>) {
        val issueTrackerProjects = projects.filterIsInstance<IssueTrackerProject>()
        val gitProjects = projects.filterIsInstance<GitProject>()
        pullRequestRegistry.all.forEach {
            it.commits = getCommits(gitProjects, it.commitIds)
            it.issues = getLinkedIssues(issueTrackerProjects, "${it.title} ${it.head.ref} ${it.body}")
            it.issues.forEach { issue -> issue.pullRequests += it }

            it.base.commit = getCommit(gitProjects, it.base.commitId)
            it.base.issue = getLinkedIssues(issueTrackerProjects, it.base.ref).firstOrNull()

            it.head.commit = getCommit(gitProjects, it.head.commitId)
            it.head.issue = getLinkedIssues(issueTrackerProjects, it.head.ref).firstOrNull()

            addPullRequestToCommits(it)
        }
        simpleBranchRegistry.all.forEach {
            it.commit = gitProjects.mapNotNull { project -> project.commitRegistry.getById(it.commitId) }.firstOrNull()
            it.issue = getLinkedIssues(issueTrackerProjects, it.ref).firstOrNull()
        }

    }

    private fun getLinkedIssues(projects: List<IssueTrackerProject>, content: String) =
            projects.flatMap {
                it.issueRegistry.allDetailedIssues
            }.filter { issue -> getRegexWithWordBoundaryGroups(issue.id).containsMatchIn(content) }

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
}