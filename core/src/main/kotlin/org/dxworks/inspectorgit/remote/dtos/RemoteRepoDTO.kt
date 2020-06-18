package org.dxworks.inspectorgit.remote.dtos

class RemoteRepoDTO(
        val id: Number,
        val name: String,
        val fullName: String,
        val owner: RemoteUserDTO
)
