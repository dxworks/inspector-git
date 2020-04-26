package org.dxworks.inspectorgit.core

import org.dxworks.inspectorgit.core.model.*
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.enums.ChangeType

class TestChangeFactory(private val gitClient: GitClient) : ChangeFactory() {
    override fun create(commit: Commit,
                        type: ChangeType,
                        oldFileName: String,
                        newFileName: String,
                        file: File,
                        parentCommit: Commit?,
                        hunks: List<Hunk>,
                        parentChange: Change?): Change =
            TestChange(commit = commit,
                    type = type,
                    oldFileName = oldFileName,
                    newFileName = newFileName,
                    file = file,
                    parentCommit = parentCommit,
                    hunks = hunks,
                    parentChange = parentChange,
                    gitClient = gitClient)
}