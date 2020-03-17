package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.dto.CommitNodeDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import java.nio.file.Paths

class CommitGraphTool(private val gitClient: GitClient) {


    fun createCommitGraph(): MutableMap<String, CommitNodeDTO> {
        val commitsMap: MutableMap<String, CommitNodeDTO> = HashMap()
        val lines = gitClient.getCommitLinks()
        var remainingLines = lines
        while (remainingLines.isNotEmpty()) {
            val commitLines = remainingLines.take(2)
            remainingLines = remainingLines.drop(2)

            val (id, parentIds) = extractIdAndParentIds(commitLines)
            val commitNodeDTO = CommitNodeDTO(id, parentIds.map { commitsMap[it]!! })

            commitNodeDTO.parents.forEach { it.addChild(commitNodeDTO) }
            commitsMap[commitNodeDTO.id] = commitNodeDTO
        }

        return commitsMap
    }

    private fun extractIdAndParentIds(commitLines: List<String>): Pair<String, List<String>> {
        return Pair(commitLines[0].removePrefix(IGLogConstants.commitIdPrefix), commitLines[1].split(" ").filter { it.isNotEmpty() })
    }
}

fun main() {
    val kafkaPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\kafkaRepo\\kafka")
    val commitGraph = CommitGraphTool(GitClient(kafkaPath))
}