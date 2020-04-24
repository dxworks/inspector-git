package org.dxworks.inspectorgit.model

abstract class Account(
        val name: String,
        val project: Project,
        var delveloper: Developer? = null
) {
    abstract val id: String
}