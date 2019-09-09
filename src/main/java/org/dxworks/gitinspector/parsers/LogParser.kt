package org.dxworks.gitinspector.parsers

import lombok.extern.slf4j.Slf4j
import org.dxworks.dto.ProjectDTO
import org.dxworks.gitinspector.GitClient
import org.slf4j.LoggerFactory

@Slf4j
class LogParser(private val gitClient: GitClient) : GitParser<ProjectDTO> {
    companion object {
        private val LOG = LoggerFactory.getLogger(LogParser::class.java)
    }

    private val commit = "commit: "

    override fun parse(lines: MutableList<String>): ProjectDTO {
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
        LOG.info("Found ${commits.size} commits")
        return ProjectDTO(commits.map { CommitParserFactory.create(it, gitClient).parse(it) }.reversed())
    }
}