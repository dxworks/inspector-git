package org.dxworks.inspectorgit.core

import org.dxworks.inspectorgit.core.model.Change
import org.dxworks.inspectorgit.core.model.Commit
import org.dxworks.inspectorgit.core.model.File
import org.dxworks.inspectorgit.core.model.Hunk
import org.dxworks.inspectorgit.gitclient.enums.ChangeType

class SimpleChangeFactory : ChangeFactory() {
    override fun create(commit: Commit,
                        type: ChangeType,
                        oldFileName: String,
                        newFileName: String,
                        file: File,
                        parentCommit: Commit?,
                        hunks: List<Hunk>,
                        parentChange: Change?): Change =
            Change(commit = commit,
                    type = type,
                    oldFileName = oldFileName,
                    newFileName = newFileName,
                    file = file,
                    parentCommit = parentCommit,
                    hunks = hunks,
                    parentChange = parentChange)
}