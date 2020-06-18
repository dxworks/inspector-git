package org.dxworks.inspectorgit.remote.dtos

class RemoteCommitInfoDTO(
        val id: String,
        val author: RemoteUserDTO,
        val committer: RemoteUserDTO
)
