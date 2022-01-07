package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.model.remote.RemoteGitProject
import org.dxworks.inspectorgit.registries.DeveloperRegistry
import org.dxworks.inspectorgit.transformers.linkers.ProjectLinkers
import org.springframework.stereotype.Component

@Component
class LoadedSystem {
    private val projectLinkers = ProjectLinkers()

    final lateinit var id: String
        private set
    final lateinit var name: String
        private set
    final lateinit var projects: Map<String, Project>
        private set

    val gitProjects: Map<String, GitProject>
        get() = getProjectsByType<GitProject>()
    val issueProjects: Map<String, IssueTrackerProject>
        get() = getProjectsByType<IssueTrackerProject>()
    val remoteProjects: Map<String, RemoteGitProject>
        get() = getProjectsByType<RemoteGitProject>()

    val developerRegistry = DeveloperRegistry()

    private inline fun <reified T : Project> getProjectsByType() =
        projects.values.filterIsInstance<T>().associateBy { it.name }


    val isSet get() = this::id.isInitialized

    fun set(id: String, name: String, projects: List<Project>) {
        this.id = id
        this.name = name

        val gitProjects = projects.filterIsInstance<GitProject>()
        val issueProjects = projects.filterIsInstance<IssueTrackerProject>()
        val remoteProjectsByName = projects.filterIsInstance<RemoteGitProject>().associateBy { it.name }

        gitProjects.forEach { git ->
            remoteProjectsByName["${git.name}-remote"]?.also { projectLinkers.link(git, it) }
                    ?: remoteProjectsByName.values.forEach { projectLinkers.link(git, it) }
        }
        issueProjects.forEach { issue ->
            gitProjects.forEach { projectLinkers.link(it, issue) }
            remoteProjectsByName.values.forEach { projectLinkers.link(issue, it) }
        }

        this.projects = projects.associateBy { it.name }
    }
}
