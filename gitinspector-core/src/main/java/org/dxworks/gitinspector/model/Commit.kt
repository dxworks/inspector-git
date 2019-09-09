package org.dxworks.gitinspector.model

import java.util.*

data class Commit(var id: String, var message: String, var date: Date, var author: Author, var parents: List<Commit>, var changes: List<Change>) {
    val isMergeCommit: Boolean
        get() = parents.size > 1

}
