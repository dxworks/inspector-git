package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import com.google.gson.Gson
import org.dxworks.inspectorgit.client.dto.GitLogDTO
import org.dxworks.inspectorgit.persistence.dto.ProjectDTO
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Lob

@Entity
data class ProjectEntity(
        @Column(unique = true)
        val name: String,
        @Lob
        val gitLogDtoString: String
) : BaseEntity<UUID>(UUID.randomUUID()) {
    fun toDto() = ProjectDTO(name, Gson().fromJson(gitLogDtoString, GitLogDTO::class.java))

    companion object {
        fun fromDto(projectDTO: ProjectDTO) = ProjectEntity(projectDTO.name, Gson().toJson(projectDTO.gitLogDTO))
    }
}