package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["systemId"])])
class LocalSystemEntity(
        @Column
        var systemId: String,

        @Column(unique = true)
        var name: String,

        @ElementCollection(targetClass = String::class)
        var sorces: List<String>
) : BaseEntity<UUID>(UUID.randomUUID()) {
}