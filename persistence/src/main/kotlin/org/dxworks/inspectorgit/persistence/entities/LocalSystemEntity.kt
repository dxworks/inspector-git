package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["systemId"])])
class LocalSystemEntity(
        @Column
        var systemId: String,

        @Column(unique = true)
        var name: String
) : BaseEntity<UUID>(UUID.randomUUID()) {
}