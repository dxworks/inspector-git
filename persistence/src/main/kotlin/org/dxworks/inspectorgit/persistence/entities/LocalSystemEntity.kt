package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["systemId"])])
class LocalSystemEntity() : BaseEntity<UUID>(UUID.randomUUID()) {

    @Column
    lateinit var systemId: String

    @Column(unique = true)
    lateinit var name: String

    @ElementCollection(targetClass = String::class)
    lateinit var sources: List<String>

    constructor(systemId: String, name: String, sources: List<String>) : this() {
        this.systemId = systemId
        this.name = name
        this.sources = sources
    }
}