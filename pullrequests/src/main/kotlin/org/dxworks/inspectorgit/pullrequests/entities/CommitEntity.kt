package org.dxworks.inspectorgit.pullrequests.entities

import org.springframework.data.annotation.CreatedDate
import java.util.*
import javax.persistence.*

@Entity
class CommitEntity(
        @Id
        @GeneratedValue
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

        @Column(columnDefinition = "TEXT")
        val message: String
)