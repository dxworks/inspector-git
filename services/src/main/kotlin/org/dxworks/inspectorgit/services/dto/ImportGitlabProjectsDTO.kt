package org.dxworks.inspectorgit.services.dto

data class ImportGitlabProjectsDTO(val swProjects: List<SwProjectDTO>,
                                   val credentials: GitlabCredentialsDTO)