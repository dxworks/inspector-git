package org.dxworks.gitinspector.model

import org.dxworks.gitinspector.registries.AuthorRegistry
import org.dxworks.gitinspector.registries.CommitRegistry
import org.dxworks.gitinspector.registries.FileRegistry

class Project(val projectID: String) {

    val authorRegistry = AuthorRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()
}
