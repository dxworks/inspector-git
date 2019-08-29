package org.dxworks.gitsecond.data

import java.util.*

data class CommitData(var id: String, var message: String, var authorName: String, var authorEmail: String, var date: Date, var parentIds: List<String>, var changeSets: List<ChangesData>) {

    val isMergeCommit: Boolean
        get() = parentIds.size >= 2
}
