package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.AbstractRegistry

abstract class Project {
    private val linkedProjects: MutableSet<Project> = HashSet()
    abstract val name: String
    abstract val accountRegistry: AbstractRegistry<Account, String>
    fun link(projects: List<Project>) {
        projects.filterNot { linkedProjects.contains(it) }.also {
            internalLink(it)
            linkedProjects.addAll(it)
        }
    }

    protected abstract fun internalLink(projects: List<Project>)
    abstract fun unlink(projects: List<Project>)
}
