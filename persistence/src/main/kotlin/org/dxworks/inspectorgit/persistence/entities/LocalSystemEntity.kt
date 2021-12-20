package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["systemId"])])
class LocalSystemEntity() : BaseEntity<UUID>(UUID.randomUUID()) {

    @Column(unique = true)
    lateinit var systemId: String

    @Column
    lateinit var name: String

    @ElementCollection(targetClass = String::class)
    lateinit var sources: List<String>

    @ElementCollection(targetClass = String::class)
    lateinit var issues: List<String>

    @ElementCollection(targetClass = String::class)
    lateinit var remotes: List<String>

    constructor(systemId: String, name: String, sources: List<String>, issues: List<String>, remotes: List<String>) : this() {
        this.systemId = systemId
        this.name = name
        this.sources = sources
        this.issues = issues
        this.remotes = remotes
    }
}
