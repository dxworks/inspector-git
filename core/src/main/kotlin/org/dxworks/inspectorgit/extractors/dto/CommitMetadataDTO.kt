package org.dxworks.inspectorgit.extractors.dto

import org.dxworks.inspectorgit.gitclient.dto.CommitInfoDTO

class CommitMetadataDTO(
        id: String,
        parentIds: List<String>,
        authorName: String,
        authorEmail: String,
        authorDate: String,
        committerName: String,
        committerEmail: String,
        committerDate: String,
        message: String,
        var changeMetadataList: List<ChangeMetadataDTO>
) : CommitInfoDTO(
        id,
        parentIds,
        authorName,
        authorEmail,
        authorDate,
        committerName,
        committerEmail,
        committerDate,
        message
) {
    constructor(commitInfoDTO: CommitInfoDTO,changeMetadataList: List<ChangeMetadataDTO>) : this(
            commitInfoDTO.id,
            commitInfoDTO.parentIds,
            commitInfoDTO.authorName,
            commitInfoDTO.authorEmail,
            commitInfoDTO.authorDate,
            commitInfoDTO.committerName,
            commitInfoDTO.committerEmail,
            commitInfoDTO.committerDate,
            commitInfoDTO.message,
            changeMetadataList
    )
}
