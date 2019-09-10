package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.GitClient
import org.dxworks.gitinspector.dto.ChangeDTO
import org.dxworks.gitinspector.parsers.abstracts.CommitParser

class MergeCommitParser(private val gitClient: GitClient) : CommitParser() {

    override fun extractChanges(lines: MutableList<String>, commitId: String): List<ChangeDTO> {
        val changes = getChanges(lines)
        val changedFiles = gitClient.affectedFiles(commitId)
        val blames = changedFiles.filter { it.isNotBlank() }.map { gitClient.blame(commitId, it) }.filter { it.isNotEmpty() }
        return blames.mapIndexed { index, blame -> BlameParser(blame).parse(changes[index]) }
    }
}