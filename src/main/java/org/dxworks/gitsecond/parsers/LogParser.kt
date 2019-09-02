package org.dxworks.gitsecond.parsers

import java.nio.file.Files
import java.nio.file.Paths

class LogParser(log: String) {
    private val lines:List<String> = Files.readAllLines(Paths.get(log)).filter { it.isNotBlank() }

    private val COMMIT = "commit"

    init {
        val commits: MutableList<MutableList<String>> = ArrayList()
        var currentCommitLines: MutableList<String> = ArrayList()
        lines.forEach() {
            if (it.startsWith(COMMIT)) {
                currentCommitLines = ArrayList()
                commits.add(currentCommitLines)
            }
                currentCommitLines.add(it)
        }
        val commitParsers = commits.map { CommitParser(it) }
                println(commitParsers)
    }
}

fun main() {
    LogParser(System.getProperty("user.home") + "/Documents/dx/testLog.log")
}