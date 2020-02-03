package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.CommitDTO
import org.dxworks.inspectorgit.gitClient.parsers.GitParser
import org.dxworks.inspectorgit.utils.devNull

class MergeCommitParser(private val commitsGroup: List<List<String>>, private val gitClient: GitClient) : GitParser<CommitDTO> {
    override fun parse(lines: List<String>): CommitDTO {
        val commitDTOs = commitsGroup.map { SimpleCommitParser().parse(it) }
        val targetCommitDTO = commitDTOs.first()
        val orderedParentIds =
                if (commitDTOs.size < targetCommitDTO.parentIds.size)
                    findCorrectParents(targetCommitDTO)
                else
                    targetCommitDTO.parentIds
        commitDTOs.forEachIndexed { i, it ->
            it.changes.forEach { changeDTO ->
                changeDTO.parentCommitId = orderedParentIds[i]
            }
        }
        val changeDTOs = commitDTOs.flatMap { it.changes }
        val groupByChangeType = changeDTOs.groupBy { it.newFileName == devNull }
        val deleteChanges = groupByChangeType.filter { it.key }.flatMap { it.value }
        val changesPerFile = groupByChangeType.filter { !it.key }
                .flatMap { it.value }.groupBy { it.newFileName }
        targetCommitDTO.changes = changesPerFile.flatMap { it.value } + deleteChanges
        return targetCommitDTO
    }

    private fun findCorrectParents(targetCommitDTO: CommitDTO): List<String> =
            targetCommitDTO.parentIds.filter {
                gitClient.diffFileNames(it, targetCommitDTO.id).isNotEmpty()
            }
}