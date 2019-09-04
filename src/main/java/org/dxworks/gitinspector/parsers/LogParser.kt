package org.dxworks.gitinspector.parsers

import org.dxworks.dto.CommitDTO
import org.dxworks.gitinspector.GitClient

class LogParser(gitClient: GitClient) {
    private val commit = "commit: "
    private val lines = gitClient.getLogs()

    val commits: List<CommitDTO>

    init {
        val commits: MutableList<MutableList<String>> = ArrayList()
        var currentCommitLines: MutableList<String> = ArrayList()
        lines.forEach {
            if (it.startsWith(commit)) {
                currentCommitLines = ArrayList()
                commits.add(currentCommitLines)
            }
            currentCommitLines.add(it)
        }
        this.commits = commits.map { CommitParserFactory.create(it, gitClient).parse(it) }
    }
}