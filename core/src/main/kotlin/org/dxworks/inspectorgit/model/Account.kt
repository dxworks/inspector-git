package org.dxworks.inspectorgit.model

abstract class Account(
        val name: String,
        val project: Project,
        var developer: Developer? = null
) {
    abstract val id: String
}