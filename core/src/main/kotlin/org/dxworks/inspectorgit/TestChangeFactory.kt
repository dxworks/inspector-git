package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.model.*

class TestChangeFactory(private val gitClient: GitClient) : ChangeFactory() {
    override fun create(commit: Commit,
                        type: ChangeType,
                        oldFileName: String,
                        newFileName: String,
                        file: File,
                        parentCommit: Commit?,
                        lineChanges: MutableList<LineChange>,
                        parentChange: Change?): Change =
            TestChange(commit = commit,
                    type = type,
                    oldFileName = oldFileName,
                    newFileName = newFileName,
                    file = file,
                    parentCommit = parentCommit,
                    lineChanges = lineChanges,
                    parentChange = parentChange,
                    gitClient = gitClient)
}