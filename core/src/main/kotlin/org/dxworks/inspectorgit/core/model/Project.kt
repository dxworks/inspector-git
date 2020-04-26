package org.dxworks.inspectorgit.core.model

import org.dxworks.inspectorgit.core.registries.AuthorRegistry
import org.dxworks.inspectorgit.core.registries.CommitRegistry
import org.dxworks.inspectorgit.core.registries.FileRegistry

class Project(val name: String) {
    val authorRegistry = AuthorRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()
}
