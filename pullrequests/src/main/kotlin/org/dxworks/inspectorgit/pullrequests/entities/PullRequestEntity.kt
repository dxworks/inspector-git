package org.dxworks.inspectorgit.pullrequests.entities

import java.util.*
import javax.persistence.*


@Entity
@Table(name = "PullRequest")
class PullRequestEntity(
        @Id
        val id: Long,

        @ManyToOne
        @JoinColumn(name = "projectId")
        val project: PRProjectEntity,

        @Column
        val title: String,

        @Column
        val status: String,

        @Temporal(TemporalType.TIMESTAMP)
        val creationDate: Date,

        @Temporal(TemporalType.TIMESTAMP)
        val mergeDate: Date,

        @Temporal(TemporalType.TIMESTAMP)
        val closeDate: Date,

        @ManyToOne
        @JoinColumn(name = "creatorId")
        val creatorDev: DeveloperEntity,

        @ManyToOne
        @JoinColumn(name = "mergerId")
        val mergeDev: DeveloperEntity,

        @OneToMany(mappedBy = "pullRequest", cascade = [CascadeType.ALL])
        val comments: List<CommentEntity>,

        @OneToMany(mappedBy = "pullRequest", cascade = [CascadeType.ALL])
        val commits: List<CommitEntity>,

        @OneToMany(mappedBy = "pullRequest", cascade = [CascadeType.ALL])
        val filesModified: List<FileEntity>,

        @Column
        val initBranch: String,

        @Column
        val finalBranch: String
)
