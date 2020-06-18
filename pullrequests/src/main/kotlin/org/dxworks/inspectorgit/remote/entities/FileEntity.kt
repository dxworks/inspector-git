package org.dxworks.inspectorgit.remote.entities

import BaseEntity
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class FileEntity() : BaseEntity<UUID>(UUID.randomUUID()) {

    @Column
    lateinit var fileName: String

    @ManyToOne
//    @JoinColumn(name = "pullRequestId")
    lateinit var pullRequest: PullRequestEntity

    @Column
    var additions: Int = 0

    @Column
    var changes: Int = 0

    @Column
    var deletions: Int = 0


    constructor(id: UUID, fileName: String, pullRequest: PullRequestEntity, additions: Int, changes: Int, deletions: Int) : this() {
        this.fileName = fileName
        this.pullRequest = pullRequest
        this.additions = additions
        this.changes = changes
        this.deletions = deletions
    }
}
