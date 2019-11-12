package org.dxworks.inspectorgit.pullrequests.entities

import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
class CommentEntity(
        @Id
        val id: Long,

        @ManyToOne
        @JoinColumn(name = "developerId")

        val author: DeveloperEntity,
        @ManyToOne

        @JoinColumn(name = "pullRequestId")
        val pullRequest: PullRequestEntity,

        @CreatedDate
        @Temporal(TemporalType.TIMESTAMP)
        val timestamp: Date,

        @Column
        val parentId: Long,

        @Column
        val file: String,

        @Column
        val lineOfCode: Int = 0,

        @Column(columnDefinition = "TEXT")
        val content: String
)

