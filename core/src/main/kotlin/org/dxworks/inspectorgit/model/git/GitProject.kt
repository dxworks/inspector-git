package org.dxworks.inspectorgit.model.git

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.model.remote.RemoteGitProject
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.git.CommitRegistry
import org.dxworks.inspectorgit.registries.git.FileRegistry
import org.dxworks.inspectorgit.transformers.getRegexWithWordBoundaryGroups
import org.slf4j.LoggerFactory

class GitProject(override val name: String) : Project() {

    companion object {
        private val LOG = LoggerFactory.getLogger(GitProject::class.java)
    }

    override val accountRegistry = AccountRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()

    override fun internalLink(projects: List<Project>) {
        val remoteGitProjects = projects.filterIsInstance<RemoteGitProject>()
        val issueTrackerProjects = projects.filterIsInstance<IssueTrackerProject>()

        LOG.info("Linking GIT project $name with Remote git projects")

        remoteGitProjects.flatMap { it.simpleBranchRegistry.all }
                .forEach { it.commit = commitRegistry.getById(it.commitId) }

        remoteGitProjects.flatMap { it.commitRemoteInfoRegistry.all }
                .forEach { commitRegistry.getById(it.commitId)?.remoteInfo = it }

        remoteGitProjects.flatMap { it.pullRequestRegistry.all }
                .forEach { pr ->
                    pr.commits += pr.commitIds
                            .mapNotNull { commitRegistry.getById(it) }
                            .onEach { it.pullRequests += pr }
                }

        LOG.info("Linking GIT project $name with JIRA projects")
        val taskPrefixes = issueTrackerProjects.flatMap { it.issueRegistry.allDetailedIssues }.map { it.id.substringBefore("-") }.distinct()

        commitRegistry.all.forEach { commit ->
            taskPrefixes.forEach { prefix ->
                val issues = getRegexWithWordBoundaryGroups("$prefix-[0-9]+").findAll(commit.message)
                        .mapNotNull { it.groups[1]?.value }
                        .mapNotNull { issueID ->
                            issueTrackerProjects
                                    .map { it.issueRegistry }
                                    .mapNotNull { it.getById(issueID) }
                                    .firstOrNull()
                        }.onEach {
                            it.commits += commit
                        }.toList()

                commit.issues += issues
            }
        }
    }

    override fun unlink(projects: List<Project>) {
    }
}
