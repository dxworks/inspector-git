package org.dxworks.inspectorgit.model.git

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.model.remote.RemoteGitProject
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.git.CommitRegistry
import org.dxworks.inspectorgit.registries.git.FileRegistry
import org.dxworks.inspectorgit.transformers.getRegexWithWordBoundaryGroups

class GitProject(override val name: String) : Project {
    override val accountRegistry = AccountRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()


    override fun link(projects: List<Project>) {
        val remoteGitProjects = projects.filterIsInstance<RemoteGitProject>()
        val issueTrackerProjects = projects.filterIsInstance<IssueTrackerProject>()

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

        val taskPrefixes = issueTrackerProjects.flatMap { it.issueRegistry.allDetailedIssues }.map { it.id.substringBefore("-") }.distinct()

        commitRegistry.all.forEach { commit ->
            taskPrefixes.forEach { prefix ->
                val issues = getRegexWithWordBoundaryGroups("$prefix-[0-9]+").findAll(commit.message)
                        .mapNotNull { it.groups[1]?.value }
                        .map { issueID ->
                            issueTrackerProjects
                                    .map { it.issueRegistry }
                                    .mapNotNull { it.getById(issueID) }
                                    .first()
                        }.onEach {
                            it.commits += commit
                        }.toList()

                commit.issues += issues
            }
        }

//        issueTrackerProjects.flatMap { it.issueRegistry.allDetailedIssues }
//                .forEach { issue ->
//                    issue.commits += commitRegistry.all
//                .filter { getRegexWithWordBoundaryGroups(issue.id).containsMatchIn(it.message) }
//                            .onEach { it.issues += issue }
//                }
    }

    override fun unlink(projects: List<Project>) {
    }
}
