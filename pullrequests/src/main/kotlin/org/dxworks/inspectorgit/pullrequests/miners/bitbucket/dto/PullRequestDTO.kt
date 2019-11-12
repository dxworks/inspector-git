package org.dxworks.inspectorgit.pullrequests.miners.bitbucket.dto

data class PullRequestDTO(
        val title: String,
        val id: Long,
        val author: AuthorDTO,
        val created_on: String,
        val updated_on: String,
        val state: String,
        val source: PullRequestBranchWrapperDTO,
        val destination: PullRequestBranchWrapperDTO,
        val closed_by: AuthorDTO)