package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["name", "platform"])])
data class IntegrationEntity(
        @Column
        val name: String,

        @Column
        val platform: String,

        @Column
        val username: String,

        @Column
        val url: String,

        @Column
        val password: String?
) : BaseEntity<UUID>(UUID.randomUUID())