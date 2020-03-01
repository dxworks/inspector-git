package org.dxworks.inspectorgit.gitclient.parsers.impl

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.dto.CommitDTO
import org.dxworks.inspectorgit.gitclient.parsers.GitParser

class MergeCommitParser(private val commitsGroup: List<List<String>>, private val gitClient: GitClient) : GitParser<CommitDTO> {
    override fun parse(lines: List<String>): CommitDTO {
        val commitDTOs = commitsGroup.map { SimpleCommitParser().parse(it) }
        val targetCommitDTO = commitDTOs.first()
        val orderedParentIds =
                if (commitDTOs.size < targetCommitDTO.parentIds.size)
                    filterParentIds(targetCommitDTO)
                else
                    targetCommitDTO.parentIds
        targetCommitDTO.changes = commitDTOs.mapIndexed { i, it ->
            it.changes.onEach { changeDTO -> changeDTO.parentCommitId = orderedParentIds[i] }
        }.flatten()
        return targetCommitDTO
    }

    private fun filterParentIds(targetCommitDTO: CommitDTO): List<String> =
            targetCommitDTO.parentIds.filter {
                gitClient.diffFileNames(it, targetCommitDTO.id).isNotEmpty()
            }
}