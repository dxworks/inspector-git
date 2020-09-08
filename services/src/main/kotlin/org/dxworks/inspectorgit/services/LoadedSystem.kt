package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.model.remote.RemoteGitProject
import org.dxworks.inspectorgit.registries.DeveloperRegistry
import org.springframework.stereotype.Component

@Component
class LoadedSystem {
    final lateinit var id: String
        private set
    final lateinit var name: String
        private set
    final lateinit var projects: Map<String, Project>
        private set

    val gitProjects: Map<String, GitProject>
        get() = getProjectsByType()
    val issueProjects: Map<String, IssueTrackerProject>
        get() = getProjectsByType()
    val remoteProjects: Map<String, RemoteGitProject>
        get() = getProjectsByType()

    val developerRegistry = DeveloperRegistry()

    private inline fun <reified T : Project> getProjectsByType() =
            projects.values.filterIsInstance<T>().map { Pair(it.name, it) }.toMap()


    val isSet get() = this::id.isInitialized

    fun set(id: String, name: String, projects: List<Project>) {
        this.id = id
        this.name = name
        this.projects = projects.onEach { it.link(projects) }.map { Pair(it.name, it) }.toMap()
    }
}