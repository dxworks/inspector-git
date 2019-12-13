package org.dxworks.inspectorgit.pullrequests.entities

import BaseEntity
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
class CommitEntity() : BaseEntity<UUID>(UUID.randomUUID()) {

    @ManyToOne
//    @JoinColumn(name = "developerId")
    lateinit var author: DeveloperEntity

    @ManyToOne
//    @JoinColumn(name = "pullRequestId")
    lateinit var pullRequest: PullRequestEntity

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    lateinit var timestamp: Date

    @Column(columnDefinition = "TEXT")
    lateinit var message: String


    constructor(author: DeveloperEntity, pullRequest: PullRequestEntity, timestamp: Date, message: String) : this() {
        this.author = author
        this.pullRequest = pullRequest
        this.timestamp = timestamp
        this.message = message
    }
}
