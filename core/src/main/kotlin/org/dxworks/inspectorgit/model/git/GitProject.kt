package org.dxworks.inspectorgit.model.git

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.git.CommitRegistry
import org.dxworks.inspectorgit.registries.git.FileRegistry

class GitProject(override val name: String) : Project() {
    override val accountRegistry = AccountRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()
}
