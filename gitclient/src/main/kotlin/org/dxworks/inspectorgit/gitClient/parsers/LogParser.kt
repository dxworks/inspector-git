package org.dxworks.inspectorgit.gitClient.parsers

import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.slf4j.LoggerFactory

class LogParser(private val gitClient: GitClient) : GitParser<GitLogDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(LogParser::class.java)
    }

    private val commit = "commit: "

    override fun parse(lines: List<String>): GitLogDTO {
        val commits = extractCommits(lines)
        LOG.info("Found ${commits.size} commits")
        val idToCommitsMap = groupCommits(commits)
        return GitLogDTO(idToCommitsMap.map { CommitParserFactory.createAndParse(it.value, gitClient) }.reversed())
    }

    private fun groupCommits(commits: List<List<String>>) =
            commits.groupBy { getCommitId(it) }

    private fun getCommitId(it: List<String>) = it[0].removePrefix("commit: ")

    private fun extractCommits(lines: List<String>): List<List<String>> {
        val commits: MutableList<MutableList<String>> = ArrayList()
        var currentCommitLines: MutableList<String> = ArrayList()
        LOG.info("Extracting commits")
        lines.forEach {
            if (it.startsWith(commit)) {
                currentCommitLines = ArrayList()
                commits.add(currentCommitLines)
            }
            currentCommitLines.add(it)
        }
        return commits
    }
}