package org.dxworks.gitsecond.parsers

import org.dxworks.gitsecond.dto.ChangeDTO

class MergeCommitParser(lines: MutableList<String>, private val gitInfoGatherer: GitInfoGatherer) : CommitParser(lines) {

    override fun extractChanges(): List<ChangeDTO> {
        val changes = getChanges()
        val blames = changes.map { gitInfoGatherer.blame(commitId, extractNewFileName(it)) }
        return blames.mapIndexed { index, blame -> BlameParser(blame, changes[index]).parse().change }
    }

    private fun extractNewFileName(diff: MutableList<String>): String {
        val newFileNameLinePrefix = "+++ b/"
        val newNameLineIndex = diff.indexOfFirst { it.startsWith(newFileNameLinePrefix) }
        return lines[newNameLineIndex].removePrefix(newFileNameLinePrefix)
    }
}