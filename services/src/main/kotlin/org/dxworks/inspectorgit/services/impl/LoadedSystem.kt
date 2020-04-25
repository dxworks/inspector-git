package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.GitAccount
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.CommitRegistry
import org.dxworks.inspectorgit.registries.FileRegistry
import org.springframework.stereotype.Component

@Component
class LoadedSystem {
    final lateinit var id: String
        private set
    final lateinit var name: String
        private set
    final lateinit var projects: Map<String, Project>
        private set
    final lateinit var accountRegistry: AccountRegistry
        private set
    final lateinit var commitRegistry: CommitRegistry
        private set
    final lateinit var fileRegistry: FileRegistry
        private set

    val isSet get() = this::id.isInitialized

    fun set(id: String, name: String, projects: List<Project>) {
        this.id = id
        this.name = name
        this.projects = projects.map { Pair(it.name, it) }.toMap()

        accountRegistry = AccountRegistry()
        accountRegistry.addAll(projects.flatMap { it.accountRegistry.getAll<GitAccount>() })

        commitRegistry = CommitRegistry()
        commitRegistry.addAll(projects.flatMap { it.commitRegistry.all })

        fileRegistry = FileRegistry()
        fileRegistry.addAll(projects.flatMap { it.fileRegistry.all })
    }
}