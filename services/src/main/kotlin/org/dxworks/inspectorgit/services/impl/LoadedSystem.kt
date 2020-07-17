package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.model.ComposedProject
import org.dxworks.inspectorgit.model.git.GitAccount
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.git.CommitRegistry
import org.dxworks.inspectorgit.registries.git.FileRegistry
import org.springframework.stereotype.Component

@Component
class LoadedSystem {
    final lateinit var id: String
        private set
    final lateinit var name: String
        private set
    final lateinit var projects: Map<String, ComposedProject>
        private set
    final lateinit var accountRegistry: AccountRegistry
        private set
    final lateinit var commitRegistry: CommitRegistry
        private set
    final lateinit var fileRegistry: FileRegistry
        private set

    val isSet get() = this::id.isInitialized

    fun set(id: String, name: String, composedProjects: List<ComposedProject>) {
        this.id = id
        this.name = name
        this.projects = composedProjects.map { Pair(it.name, it) }.toMap()

        accountRegistry = AccountRegistry()
        accountRegistry.addAll(composedProjects.flatMap { it.accountRegistry.getAll<GitAccount>() })

        commitRegistry = CommitRegistry()
        commitRegistry.addAll(composedProjects.flatMap { it.commitRegistry.all })

        fileRegistry = FileRegistry()
        fileRegistry.addAll(composedProjects.flatMap { it.fileRegistry.all })
    }
}