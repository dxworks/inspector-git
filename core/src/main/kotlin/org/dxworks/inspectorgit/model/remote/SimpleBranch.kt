package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit

open class SimpleBranch(
        val commit: Commit?,
        val commitId: String,
        val ref: String
)