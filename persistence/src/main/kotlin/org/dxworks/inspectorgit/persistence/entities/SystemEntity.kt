package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
data class SystemEntity(
        @Column(unique = true)
        val name: String,

        @Column
        val systemId: String,

        @OneToMany
        val projects: List<ProjectEntity>
) : BaseEntity<UUID>(UUID.randomUUID())