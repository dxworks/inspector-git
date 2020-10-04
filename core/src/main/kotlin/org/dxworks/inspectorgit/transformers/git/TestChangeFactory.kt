package org.dxworks.inspectorgit.transformers.git

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.model.TestChange
import org.dxworks.inspectorgit.model.git.*

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
