package org.dxworks.inspectorgit.transformers.git

import org.dxworks.inspectorgit.model.git.*

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
