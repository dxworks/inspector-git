package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity

@Entity
data class ProjectEntity(
        @Column(unique = true)
        val name: String,
        @Column
        val dtoString: String
) : BaseEntity<UUID>(UUID.randomUUID())