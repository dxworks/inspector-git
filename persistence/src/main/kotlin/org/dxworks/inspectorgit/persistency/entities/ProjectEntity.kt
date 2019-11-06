package org.dxworks.inspectorgit.persistency.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class ProjectEntity(
        @Column
        val name: String,
        val dtoString: String
) : BaseEntity<UUID>(UUID.randomUUID())