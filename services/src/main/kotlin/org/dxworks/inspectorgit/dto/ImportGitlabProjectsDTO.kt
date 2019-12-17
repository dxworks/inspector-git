package org.dxworks.inspectorgit.dto

data class ImportGitlabProjectsDTO(val swProjects: List<SwProjectDTO>,
                                   val credentials: GitlabCredentialsDTO)