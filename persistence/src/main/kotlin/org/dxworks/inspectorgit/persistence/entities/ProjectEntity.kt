package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import com.google.gson.Gson
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.persistence.dto.ProjectDTO
import java.util.*
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["name", "integrationPath"])])
data class ProjectEntity(
        @Column
        val name: String,
        @Column(unique = true)
        val path: String,

        @Column
        var branch: String,

        @Column
        val integrationPath: String,

        @Column
        val repositoryHttpUrl: String,

        @Column
        val webUrl: String,

        @Column
        var pullRequestsEnabled: Boolean,

        @Lob
        var gitLogDtoString: String?
) : BaseEntity<UUID>(UUID.randomUUID()) {
    fun toDto() = ProjectDTO(name, path, branch, integrationPath, repositoryHttpUrl, webUrl, pullRequestsEnabled, Gson().fromJson(gitLogDtoString, GitLogDTO::class.java))

    companion object {
        fun fromDto(projectDTO: ProjectDTO) = ProjectEntity(
                projectDTO.name,
                projectDTO.path,
                projectDTO.branch,
                projectDTO.integrationPath,
                projectDTO.repositoryHttpUrl,
                projectDTO.webUrl,
                projectDTO.pullRequestsEnabled,
                Gson().toJson(projectDTO.gitLogDTO)
        )
    }
}