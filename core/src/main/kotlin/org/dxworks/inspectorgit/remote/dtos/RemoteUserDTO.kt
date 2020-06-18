package org.dxworks.inspectorgit.remote.dtos

class RemoteUserDTO(
        val id: Number,
        val login: String,
        val url: String,
        val email: String?,
        val name: String?,
        val avatarUrl: String?
)