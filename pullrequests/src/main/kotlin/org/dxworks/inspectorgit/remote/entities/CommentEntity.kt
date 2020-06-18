package org.dxworks.inspectorgit.remote.entities

import BaseEntity
import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
class CommentEntity() : BaseEntity<UUID>(UUID.randomUUID()) {

    @ManyToOne
//    @JoinColumn(name = "developerId")
    lateinit var author: DeveloperEntity

    @ManyToOne
//    @JoinColumn(name = "pullRequestId")
    lateinit var pullRequest: PullRequestEntity

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    lateinit var timestamp: Date

    @Column
    var commentId: Long = 0

    @Column
    var parentId: Long = 0

    @Column
    lateinit var file: String

    @Column
    var lineOfCode: Int = 0

    @Column(columnDefinition = "TEXT")
    lateinit var content: String

    constructor(author: DeveloperEntity, pullRequest: PullRequestEntity, timestamp: Date, parentId: Long, file: String, content: String) : this() {
        this.author = author
        this.pullRequest = pullRequest
        this.timestamp = timestamp
        this.parentId = parentId
        this.file = file
        this.content = content
    }
}
