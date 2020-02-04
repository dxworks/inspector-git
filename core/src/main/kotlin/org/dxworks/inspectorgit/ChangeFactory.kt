package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.gitClient.enums.ChangeType
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.model.File
import org.dxworks.inspectorgit.model.LineChange

abstract class ChangeFactory {
    abstract fun create(commit: Commit,
                        type: ChangeType,
                        file: File,
                        parentCommits: List<Commit>,
                        fileName: String,
                        lineChanges: MutableList<LineChange>,
                        parentChange: Change?): Change
}