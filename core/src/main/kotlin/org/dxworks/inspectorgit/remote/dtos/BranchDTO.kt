package org.dxworks.inspectorgit.remote.dtos

class BranchDTO(
        val commit: String,
        val label: String,
        val ref: String,
        val user: RemoteUserDTO,
        val repo: RemoteRepoDTO
)
