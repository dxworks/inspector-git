package org.dxworks.inspectorgit.dto

import com.google.gson.Gson
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.persistence.entities.ProjectEntity

data class ProjectDTO(
        val name: String,
        var path: String?,
        val branch: String,
        val integrationPath: String,
        val repositoryHttpUrl: String?,
        val webUrl: String?,
        val pullRequestsEnabled: Boolean,
        var gitLogDTO: GitLogDTO? = null
) {
    fun toEntity() = ProjectEntity(name, path, branch, integrationPath, repositoryHttpUrl, webUrl, pullRequestsEnabled, Gson().toJson(gitLogDTO))

    companion object {
        fun fromEntity(entity: ProjectEntity) = ProjectDTO(
                entity.name,
                entity.path,
                entity.branch,
                entity.integrationPath,
                entity.repositoryHttpUrl,
                entity.webUrl,
                entity.pullRequestsEnabled,
                Gson().fromJson(entity.gitLogDtoString, GitLogDTO::class.java))
    }
}
