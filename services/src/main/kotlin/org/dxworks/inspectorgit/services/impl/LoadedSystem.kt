package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.registries.AuthorRegistry
import org.dxworks.inspectorgit.registries.CommitRegistry
import org.dxworks.inspectorgit.registries.FileRegistry
import org.springframework.stereotype.Component

@Component
class LoadedSystem {
    lateinit var id: String
    lateinit var name: String
    lateinit var projects: Map<String, Project>
    lateinit var authorRegistry: AuthorRegistry
    lateinit var commitRegistry: CommitRegistry
    lateinit var fileRegistry: FileRegistry

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