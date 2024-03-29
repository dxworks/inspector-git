package org.dxworks.inspectorgit.gitclient.dto.gitlog

import org.dxworks.inspectorgit.gitclient.dto.CommitInfoDTO

class CommitDTO(
        id: String,
        parentIds: List<String>,
        authorName: String,
        authorEmail: String,
        authorDate: String,
        committerName: String,
        committerEmail: String,
        committerDate: String,
        message: String,
        var changes: List<ChangeDTO>
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
)