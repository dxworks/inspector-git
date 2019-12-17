package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
class SystemEntity : BaseEntity<UUID>(UUID.randomUUID()) {
    @Column(unique = true)
    var name: String? = null

    @Column(unique = true)
    var systemId: String? = null

    @OneToMany
    var swProjects: List<SwProjectEntity>? = emptyList()
}
