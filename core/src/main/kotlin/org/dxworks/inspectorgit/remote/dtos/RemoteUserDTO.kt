package org.dxworks.inspectorgit.remote.dtos

class RemoteUserDTO(
        val id: Number,
        val login: String,
        val url: String,
        val email: String? = null,
        val name: String? = null,
        val avatarUrl: String? = null
)
