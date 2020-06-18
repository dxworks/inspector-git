package org.dxworks.inspectorgit.remote.dtos

class ReviewDTO(
        val user: RemoteUserDTO,
        val state: String,
        val body: String,
        val date: String
)
