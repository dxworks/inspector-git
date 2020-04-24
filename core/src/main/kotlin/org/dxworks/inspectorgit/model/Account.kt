package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.model.git.GitAccount

abstract class Account(
        val name: String,
        val project: Project,
        var gitAccount: GitAccount? = null
) {
    abstract val id: String
}