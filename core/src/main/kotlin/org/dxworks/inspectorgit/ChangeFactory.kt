package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.model.*

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