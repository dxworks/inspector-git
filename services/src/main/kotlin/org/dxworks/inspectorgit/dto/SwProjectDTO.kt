package org.dxworks.inspectorgit.dto

import com.google.gson.Gson
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.persistence.entities.SwProjectEntity

class SwProjectDTO : ProjectDTO() {
    var description: String? = null
    var path: String? = null
    var branch: String? = null
    var repositoryHttpUrl: String? = null
    var pullRequestsEnabled: Boolean = false
    var gitLogDTO: GitLogDTO? = null


    fun toEntity(): SwProjectEntity {
        val swProjectEntity = SwProjectEntity()
        swProjectEntity.name = name
        swProjectEntity.platform = platform
        swProjectEntity.integrationName = integrationName
        swProjectEntity.webUrl = webUrl
        swProjectEntity.description = description
        swProjectEntity.path = path
        swProjectEntity.branch = branch
        swProjectEntity.pullRequestsEnabled = pullRequestsEnabled
        swProjectEntity.gitLogDtoString = Gson().toJson(gitLogDTO)
        return swProjectEntity
    }

    companion object {
        fun fromEntity(swProjectEntity: SwProjectEntity, includeLogs: Boolean = true): SwProjectDTO {
            val swProjectDTO = SwProjectDTO()
            swProjectDTO.name = swProjectEntity.name
            swProjectDTO.platform = swProjectEntity.platform
            swProjectDTO.integrationName = swProjectEntity.integrationName
            swProjectDTO.webUrl = swProjectEntity.webUrl
            swProjectDTO.description = swProjectEntity.description
            swProjectDTO.path = swProjectEntity.path
            swProjectDTO.branch = swProjectEntity.branch
            swProjectDTO.pullRequestsEnabled = swProjectEntity.pullRequestsEnabled
            swProjectDTO.gitLogDTO = if (includeLogs) Gson().fromJson(swProjectEntity.gitLogDtoString, GitLogDTO::class.java) else null
            return swProjectDTO
        }
    }
}
