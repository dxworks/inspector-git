package org.dxworks.inspectorgit.remote.dtos

class PullRequestDTO(
        val id: Number,
        val number: Number,
        val title: String,
        val body: String,
        val head: BranchDTO,
        val base: BranchDTO,
        val commits: List<String>,
        val createdAt: String,
        val updatedAt: String,
        val mergedAt: String?,
        val closedAt: String?,
        val state: String,
        val createdBy: RemoteUserDTO,
        val mergedBy: RemoteUserDTO?,
        val reviews: List<ReviewDTO>,
        val comments: List<PullRequestCommentDTO>
)