package org.dxworks.inspectorgit.services.dto

import org.dxworks.inspectorgit.persistence.entities.SystemEntity

class SystemDTO {
    var name: String? = null
    var systemId: String? = null
    var projects: List<SwProjectDTO>? = emptyList()

    companion object {
        fun fromEntity(entity: SystemEntity, includeProjects: Boolean = true): SystemDTO {
            val dto = SystemDTO()
            dto.name = entity.name
            dto.systemId = entity.systemId
            dto.projects = if (includeProjects) entity.swProjects?.map { SwProjectDTO.fromEntity(it) } else null
            return dto
        }
    }
}
