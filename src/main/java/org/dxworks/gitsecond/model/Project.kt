package org.dxworks.gitsecond.model

import org.dxworks.gitsecond.registries.AuthorRegistry
import org.dxworks.gitsecond.registries.CommitRegistry
import org.dxworks.gitsecond.registries.FileRegistry

class Project(val projectID: String) {

    val authorRegistry = AuthorRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()
}
