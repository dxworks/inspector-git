package org.dxworks.gitinspector.parsers.impl

import org.dxworks.gitinspector.GitClient
import org.dxworks.gitinspector.dto.ChangeDTO
import org.dxworks.gitinspector.parsers.abstracts.CommitParser

class MergeCommitParser(private val gitInfoGatherer: GitClient) : CommitParser() {

    override fun extractChanges(lines: MutableList<String>, commitId: String): List<ChangeDTO> {
        val changes = getChanges(lines)
        val blames = changes.map { gitInfoGatherer.blame(commitId, extractNewFileName(it)) }
        return blames.mapIndexed { index, blame -> BlameParser(blame).parse(changes[index]) }
    }

    private fun extractNewFileName(lines: MutableList<String>): String {
        val newFileNameLinePrefix = "+++ b/"
        val newNameLineIndex = lines.indexOfFirst { it.startsWith(newFileNameLinePrefix) }
        return lines[newNameLineIndex].removePrefix(newFileNameLinePrefix)
    }
}