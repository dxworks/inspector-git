package org.dxworks.inspectorgit.persistence.dto

import org.dxworks.inspectorgit.client.dto.GitLogDTO

data class ProjectDTO(val name: String, val gitLogDTO: GitLogDTO)