package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["name", "integrationPath"])])
data class ProjectEntity(
        @Column
        val name: String,
        @Column(unique = true)
        val path: String?,

        @Column
        var branch: String,

        @Column
        val integrationPath: String,

        @Column
        val repositoryHttpUrl: String?,

        @Column
        val webUrl: String?,

        @Column
        var pullRequestsEnabled: Boolean,

        @Lob
        var gitLogDtoString: String?
) : BaseEntity<UUID>(UUID.randomUUID())