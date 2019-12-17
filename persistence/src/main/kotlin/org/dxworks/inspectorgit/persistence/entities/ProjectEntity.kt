package org.dxworks.inspectorgit.persistence.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class ProjectEntity : BaseEntity<UUID>(UUID.randomUUID()) {
    @Column
    var name: String? = null

    @Column
    var integrationName: String? = null

    @Column
    var platform: String? = null

    @Column
    var webUrl: String? = null
}
