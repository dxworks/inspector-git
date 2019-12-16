package org.dxworks.inspectorgit.dto

import org.dxworks.inspectorgit.dto.ProjectDTO

data class ImportGitlabProjectsDTO(val projects: List<ProjectDTO>,
                                   val credentials: GitlabCredentialsDTO)