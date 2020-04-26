package org.dxworks.inspectorgit.services.dto

import com.fasterxml.jackson.annotation.JsonProperty

class GitlabSimpleProjectResponseDTO {
    lateinit var id: String
    var description: String? = null

    var defaultBranch: String? = null
        @JsonProperty("defaultBranch")
        get
        @JsonProperty("default_branch")
        set
    var sshUrlToRepo: String? = null
        @JsonProperty("sshUrlToRepo")
        get
        @JsonProperty("ssh_url_to_repo")
        set
    var httpUrlToRepo: String? = null
        @JsonProperty("httpUrlToRepo")
        get
        @JsonProperty("http_url_to_repo")
        set
    var webUrl: String? = null
        @JsonProperty("webUrl")
        get
        @JsonProperty("web_url")
        set
    var readmeUrl: String? = null
        @JsonProperty("readmeUrl")
        get
        @JsonProperty("readme_url")
        set
    var name: String? = null
    var nameWithNamespace: String? = null
        @JsonProperty("nameWithNamespace")
        get
        @JsonProperty("name_with_namespace")
        set
    var path: String? = null
    var pathWithNamespace: String? = null
        @JsonProperty("pathWithNamespace")
        get
        @JsonProperty("path_with_namespace")
        set
    var avatarUrl: String? = null
        @JsonProperty("avatarUrl")
        get
        @JsonProperty("avatar_url")
        set
}
