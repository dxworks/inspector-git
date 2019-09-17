package org.dxworks.inspectorgit.model

import java.util.*

data class Commit(var id: String, var message: String, val authorDate: Date, val committerDate: Date, val author: Author, val committer: Author, var parents: List<Commit>, var changes: List<Change>) {
    val isMergeCommit: Boolean
        get() = parents.size > 1

}
