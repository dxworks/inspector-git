package org.dxworks.inspectorgit.pullrequests.entities

import javax.persistence.*

@Entity
@Table(name = "File")
class FileEntity(

        @Id
        @GeneratedValue
        val id: Long,

        @Column
        val fileName: String,

        @ManyToOne
        @JoinColumn(name = "pullRequestId")
        val pullRequest: PullRequestEntity,

        @Column
        val additions: Int = 0,

        @Column
        val changes: Int = 0,

        @Column
        val deletions: Int = 0
)