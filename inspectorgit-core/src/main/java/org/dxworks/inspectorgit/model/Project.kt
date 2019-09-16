package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.AuthorRegistry
import org.dxworks.inspectorgit.registries.CommitRegistry
import org.dxworks.inspectorgit.registries.FileRegistry

class Project(val projectID: String) {

    val authorRegistry = AuthorRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()
}
