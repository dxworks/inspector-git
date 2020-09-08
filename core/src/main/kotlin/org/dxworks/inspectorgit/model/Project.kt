package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.AbstractRegistry

interface Project {
    val name: String
    val accountRegistry: AbstractRegistry<Account, String>
    fun link(projects: List<Project>)
    fun unlink(projects: List<Project>)
}