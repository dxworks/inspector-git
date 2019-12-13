package org.dxworks.inspectorgit.web.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GitlabSimpleRepositoryResponseDTO(
        val id: String,
        val description: String?,
        @JsonProperty("default_branch")
        val defaultBranch: String?,
        @JsonProperty("ssh_url_to_repo")
        val sshUrlToRepo: String?,
        @JsonProperty("http_url_to_repo")
        val httpUrlToRepo: String?,
        @JsonProperty("web_url")
        val webUrl: String?,
        @JsonProperty("readme_url")
        val readmeUrl: String?,
        val name: String?,
        @JsonProperty("name_with_namespace")
        val nameWithNamespace: String?,
        val path: String?,
        @JsonProperty("path_with_namespace")
        val pathWithNamespace: String?,
        @JsonProperty("avatar_url")
        val avatarUrl: String?
)