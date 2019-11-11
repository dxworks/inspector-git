package org.dxworks.inspectorgit.client.parsers

import org.dxworks.inspectorgit.client.GitClient
import org.dxworks.inspectorgit.client.dto.GitLogDTO
import org.slf4j.LoggerFactory

class LogParser(private val gitClient: GitClient) : GitParser<GitLogDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(LogParser::class.java)
    }

    private val commit = "commit: "

    override fun parse(lines: List<String>): GitLogDTO {
        val commits = extractCommits(lines)
        LOG.info("Found ${commits.size} commits")
        return GitLogDTO(commits.map { CommitParserFactory.create(it, gitClient).parse(it) }.reversed())
    }

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