package org.dxworks.inspectorgit.model.system

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.DeveloperRegistry

class System {
    val developerRegistry = DeveloperRegistry()
    val accountRegistry = AccountRegistry()
    var projects: List<Project> = emptyList()
        private set

    fun addProject(project: Project) {
        project.link(projects)
        projects = projects + project
    }

    fun removeProject(project: Project) {
        project.unlink(projects)
        projects = projects - project
    }
}