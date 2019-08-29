package org.dxworks.gitsecond.model

import org.dxworks.gitsecond.registries.AuthorRegistry
import org.dxworks.gitsecond.registries.CommitRegistry
import org.dxworks.gitsecond.registries.FileRegistry

class Project(val projectID: String) {

    val authorRegistry: AuthorRegistry = AuthorRegistry()
    val commitRegistry: CommitRegistry = CommitRegistry()
    val fileRegistry: FileRegistry = FileRegistry()

}
