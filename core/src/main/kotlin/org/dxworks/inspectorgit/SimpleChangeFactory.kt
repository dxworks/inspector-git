package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.File
import org.dxworks.inspectorgit.model.LineChange

class SimpleChangeFactory : ChangeFactory() {
    override fun create(commit: Commit,
                        type: ChangeType,
                        file: File,
                        parentCommits: List<Commit>,
                        oldFileName: String,
                        newFileName: String,
                        lineChanges: MutableList<LineChange>,
                        parentCommit: Commit?): Change =
            Change(commit = commit,
                    type = type,
                    file = file,
                    parentCommits = parentCommits,
                    oldFileName = oldFileName,
                    newFileName = newFileName,
                    lineChanges = lineChanges,
                    parentCommit = parentCommit)
}