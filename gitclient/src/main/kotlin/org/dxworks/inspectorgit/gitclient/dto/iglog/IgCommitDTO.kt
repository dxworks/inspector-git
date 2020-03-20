package org.dxworks.inspectorgit.gitclient.dto.iglog

import org.dxworks.inspectorgit.gitclient.dto.CommitInfoDTO

class IgCommitDTO(
        id: String,
        parentIds: List<String>,
        authorName: String,
        authorEmail: String,
        authorDate: String,
        committerName: String,
        committerEmail: String,
        committerDate: String,
        message: String,
        val changes: List<IgChangeDTO>
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

}
