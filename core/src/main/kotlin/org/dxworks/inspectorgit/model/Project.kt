package org.dxworks.inspectorgit.model

interface Project {
    val name: String
    fun link(projects: List<Project>)
    fun unlink(projects: List<Project>)
}