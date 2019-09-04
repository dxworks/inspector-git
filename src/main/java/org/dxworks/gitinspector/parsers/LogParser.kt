package org.dxworks.gitinspector.parsers

import org.dxworks.dto.ProjectDTO
import org.dxworks.gitinspector.GitClient

class LogParser(private val gitClient: GitClient) : GitParser<ProjectDTO> {
    private val commit = "commit: "

    override fun parse(lines: MutableList<String>): ProjectDTO {
        val commits: MutableList<MutableList<String>> = ArrayList()
        var currentCommitLines: MutableList<String> = ArrayList()
        lines.forEach {
            if (it.startsWith(commit)) {
                currentCommitLines = ArrayList()
                commits.add(currentCommitLines)
            }
            currentCommitLines.add(it)
        }
        return ProjectDTO(commits.map { CommitParserFactory.create(it, gitClient).parse(it) }.reversed())
    }
}