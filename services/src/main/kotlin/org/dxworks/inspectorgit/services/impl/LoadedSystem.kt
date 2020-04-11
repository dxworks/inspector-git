package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.model.Project
import org.springframework.stereotype.Component

@Component
class LoadedSystem {
    private lateinit var id: String
    private lateinit var name: String
    private lateinit var projects: List<Project>

    fun set(id: String, name: String, projects: List<Project>) {
        this.id = id
        this.name = name
        this.projects = projects
    }
}