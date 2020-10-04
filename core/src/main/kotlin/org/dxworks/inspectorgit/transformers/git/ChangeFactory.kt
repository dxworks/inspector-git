package org.dxworks.inspectorgit.transformers.git

import org.dxworks.inspectorgit.model.git.*

abstract class ChangeFactory {
    abstract fun create(commit: Commit,
                        type: ChangeType,
                        oldFileName: String,
                        newFileName: String,
                        file: File,
                        parentCommit: Commit?,
                        hunks: List<Hunk>,
                        parentChange: Change?): Change
}
