package org.dxworks.inspectorgit.web.dto

data class GitlabCloneRepositoriesDTO(val repositories: List<CloneRepositoryDTO>,
                                      val credentials: GitlabCredentialsDTO)