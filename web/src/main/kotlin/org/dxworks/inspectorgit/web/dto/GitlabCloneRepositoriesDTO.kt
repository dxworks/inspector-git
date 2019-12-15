package org.dxworks.inspectorgit.web.dto

import org.dxworks.inspectorgit.persistence.dto.ProjectDTO

data class GitlabCloneRepositoriesDTO(val projects: List<ProjectDTO>,
                                      val credentials: GitlabCredentialsDTO)