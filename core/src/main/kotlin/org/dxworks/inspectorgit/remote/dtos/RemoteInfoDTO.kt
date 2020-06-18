package org.dxworks.inspectorgit.remote.dtos

class RemoteInfoDTO(
        val pullRequests: List<PullRequestDTO>,
        val commitInfos: List<RemoteCommitInfoDTO>,
        val branches: List<SimpleBranchDTO>
)
