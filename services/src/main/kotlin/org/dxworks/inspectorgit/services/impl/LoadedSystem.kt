package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.core.model.Project
import org.dxworks.inspectorgit.core.registries.AuthorRegistry
import org.dxworks.inspectorgit.core.registries.CommitRegistry
import org.dxworks.inspectorgit.core.registries.FileRegistry
import org.springframework.stereotype.Component

@Component
class LoadedSystem {
    final lateinit var id: String
        private set
    final lateinit var name: String
        private set
    final lateinit var projects: Map<String, Project>
        private set
    final lateinit var authorRegistry: AuthorRegistry
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

        authorRegistry = AuthorRegistry()
        authorRegistry.addAll(projects.flatMap { it.authorRegistry.all })

        commitRegistry = CommitRegistry()
        commitRegistry.addAll(projects.flatMap { it.commitRegistry.all })

        fileRegistry = FileRegistry()
        fileRegistry.addAll(projects.flatMap { it.fileRegistry.all })
    }
}