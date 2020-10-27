package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.AbstractRegistry

abstract class Project {
    private val linkedProjects: MutableSet<Project> = HashSet()
    abstract val name: String
    abstract val accountRegistry: AbstractRegistry<Account, String>
    fun link(other: Project) {
        linkedProjects.add(other)
    }

    fun isLinked(other: Project): Boolean {
        return linkedProjects.contains(other)
    }
}
