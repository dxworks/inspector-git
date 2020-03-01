package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.model.*

class TestChangeFactory(private val gitClient: GitClient) : ChangeFactory() {
    override fun create(commit: Commit,
                        type: ChangeType,
                        file: File,
                        parentCommits: List<Commit>,
                        lineChanges: MutableList<LineChange>,
                        parentChange: Change?): Change =
            TestChange(commit = commit,
                    type = type,
                    file = file,
                    parentCommits = parentCommits,
                    lineChanges = lineChanges,
                    parentChange = parentChange,
                    gitClient = gitClient)
}