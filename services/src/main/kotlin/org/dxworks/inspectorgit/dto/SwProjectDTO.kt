package org.dxworks.inspectorgit.dto

import com.google.gson.Gson
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.persistence.entities.SwProjectEntity

class SwProjectDTO : ProjectDTO() {
    var description: String? = null
    var path: String? = null
    var branch: String? = null
    var repositoryHttpUrl: String? = null
    var pullRequestsEnabled: Boolean = false
    var gitLogDTO: GitLogDTO? = null


    fun toEntity(): SwProjectEntity {
        val entity = SwProjectEntity()
        entity.name = name
        entity.platform = platform
        entity.integrationName = integrationName
        entity.webUrl = webUrl
        entity.description = description
        entity.path = path
        entity.branch = branch
        entity.pullRequestsEnabled = pullRequestsEnabled
        entity.imported = imported
        entity.gitLogDtoString = Gson().toJson(gitLogDTO)
        return entity
    }

    companion object {
        fun fromEntity(entity: SwProjectEntity, includeLogs: Boolean = true): SwProjectDTO {
            val dto = SwProjectDTO()
            dto.name = entity.name
            dto.platform = entity.platform
            dto.integrationName = entity.integrationName
            dto.webUrl = entity.webUrl
            dto.description = entity.description
            dto.path = entity.path
            dto.branch = entity.branch
            dto.pullRequestsEnabled = entity.pullRequestsEnabled
            dto.imported = entity.imported
            dto.gitLogDTO = if (includeLogs) Gson().fromJson(entity.gitLogDtoString, GitLogDTO::class.java) else null
            return dto
        }
    }
}
