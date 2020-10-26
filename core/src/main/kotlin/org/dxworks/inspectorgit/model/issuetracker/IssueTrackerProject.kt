package org.dxworks.inspectorgit.model.issuetracker

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.model.remote.RemoteGitProject
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueStatusCategoryRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueStatusRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueTypeRegistry
import org.dxworks.inspectorgit.transformers.getRegexWithWordBoundaryGroups

class IssueTrackerProject(override val name: String) : Project {

    override val accountRegistry = AccountRegistry()
    val issueRegistry = IssueRegistry()
    val issueTypeRegistry = IssueTypeRegistry()
    val issueStatusRegistry = IssueStatusRegistry()
    val issueStatusCategoryRegistry = IssueStatusCategoryRegistry()


    override fun link(projects: List<Project>) {
        val remoteGitProjects = projects.filterIsInstance<RemoteGitProject>()
        val gitProjects = projects.filterIsInstance<GitProject>()

        val issuePrefixes = issueRegistry.all.map { it.id.substring(0, it.id.indexOf("-")) }.distinct()

        val taskRegexList = issuePrefixes.map { getTaskRegex(it) }
        val smartCommits = gitProjects.flatMap { it.commitRegistry.all }
                .filter { taskRegexList.any { taskRegex -> taskRegex.containsMatchIn(it.message) } }
        val taskIdToSmartCommitMap = mapOfCommitsByTaskId(smartCommits, taskRegexList)
        taskIdToSmartCommitMap.forEach { (id, commits) -> commits.forEach { it.taskIds = it.taskIds + id } }

        remoteGitProjects.flatMap { it.pullRequestRegistry.all }
                .forEach { pr ->
                    issueRegistry.allDetailedIssues.forEach {
                        val taskRegex = getRegexWithWordBoundaryGroups(it.id)
                        if (taskRegex.containsMatchIn("${pr.title} ${pr.head.ref} ${pr.body}"))
                            pr.issues += it
                    }
                }
        remoteGitProjects.flatMap { it.simpleBranchRegistry.all }
                .forEach { branch ->
                    branch.issue = issueRegistry.allDetailedIssues
                            .firstOrNull { getRegexWithWordBoundaryGroups(it.id).containsMatchIn(branch.ref) }
                }

    }

    private fun getTaskRegex(prefix: String) = getRegexWithWordBoundaryGroups("$prefix-\\d+")

    private fun mapOfCommitsByTaskId(allSmartCommits: List<Commit>, taskRegexList: List<Regex>): MutableMap<String, List<Commit>> {
        val allTaskIds = allSmartCommits.flatMap {
            taskRegexList
                    .flatMap { taskRegex ->
                        taskRegex.findAll(it.message).toList()
                                .mapNotNull {
                                    it.groupValues.getOrNull(2)
                                }
                    }
        }.filter { it.isNotBlank() }.distinct()
        return allTaskIds.map { id -> id to allSmartCommits.filter { getRegexWithWordBoundaryGroups(id).containsMatchIn(it.message) } }.toMap().toMutableMap()
    }

    override fun unlink(projects: List<Project>) {
        TODO("Not yet implemented")
    }
}

