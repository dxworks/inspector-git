package org.dxworks.inspectorgit.remote.entities

import BaseEntity
import java.util.*
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
class PRProjectEntity() : BaseEntity<UUID>(UUID.randomUUID()) {

    @Column
    lateinit var repository: String

    @Column
    lateinit var owner: String

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    lateinit var developers: List<DeveloperEntity>

    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
    lateinit var pullRequests: List<PullRequestEntity>


    constructor(repository: String, owner: String, developers: List<DeveloperEntity>, pullRequests: List<PullRequestEntity>) : this() {
        this.repository = repository
        this.owner = owner
        this.developers = developers
        this.pullRequests = pullRequests
    }
}
