package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.task.DetailedTask

open class SimpleBranch(
        val commit: Commit?,
        val commitId: String,
        val ref: String,
        val task: DetailedTask?
)