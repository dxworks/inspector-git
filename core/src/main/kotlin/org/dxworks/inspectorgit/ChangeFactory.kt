package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.model.git.Change
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.git.File
import org.dxworks.inspectorgit.model.git.Hunk

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