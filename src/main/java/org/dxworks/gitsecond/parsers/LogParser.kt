package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.dto.CommitDTO
import java.nio.file.Paths

class LogParser(gitInfoGatherer: GitInfoGatherer) {
    private val lines = gitInfoGatherer.getLogs()
    private val COMMIT = "commit"

    val commits: List<CommitDTO>

    init {
        val commits: MutableList<MutableList<String>> = ArrayList()
        var currentCommitLines: MutableList<String> = ArrayList()
        lines.forEach {
            if (it.startsWith(COMMIT)) {
                currentCommitLines = ArrayList()
                commits.add(currentCommitLines)
            }
            currentCommitLines.add(it)
        }
        this.commits = commits.map { CommitParserFactory.create(it, gitInfoGatherer).parse().commit }
    }
}

fun main() {

    val process = ProcessBuilder("bash", "-c", "git --version").start()
    process.waitFor()
    String(process.inputStream.readAllBytes()).also { println(it) }

    val gitInfoGatherer = GitInfoGatherer(Paths.get(System.getProperty("user.home"), "Documents/dx/test"))
    val commits = LogParser(gitInfoGatherer).commits
    println(commits)
}