package org.dxworks.inspectorgit.pullrequests.entities

import BaseEntity
import org.eclipse.egit.github.core.PullRequest
import java.util.*
import javax.persistence.*


@Entity
class PullRequestEntity() : BaseEntity<UUID>(UUID.randomUUID()) {
    @ManyToOne
//    @JoinColumn(name = "projectId")
    lateinit var project: PRProjectEntity

    @Column
    lateinit var title: String

    @Column
    lateinit var status: String

    @Temporal(TemporalType.TIMESTAMP)
    lateinit var creationDate: Date

    @Temporal(TemporalType.TIMESTAMP)
    lateinit var mergeDate: Date

    @Temporal(TemporalType.TIMESTAMP)
    lateinit var closeDate: Date

    @ManyToOne
//    @JoinColumn(name = "creatorId")
    lateinit var creatorDev: DeveloperEntity

    @ManyToOne
//    @JoinColumn(name = "mergerId")
    lateinit var mergeDev: DeveloperEntity

    @OneToMany(mappedBy = "pullRequest", cascade = [CascadeType.ALL])
    lateinit var comments: List<CommentEntity>

    @OneToMany(mappedBy = "pullRequest", cascade = [CascadeType.ALL])
    lateinit var commits: List<CommitEntity>

    @OneToMany(mappedBy = "pullRequest", cascade = [CascadeType.ALL])
    lateinit var filesModified: List<FileEntity>

    @Column
    lateinit var initBranch: String

    @Column
    lateinit var finalBranch: String

    constructor(project: PRProjectEntity, title: String, status: String, creationDate: Date, mergeDate: Date, closeDate: Date, creatorDev: DeveloperEntity, mergeDev: DeveloperEntity, comments: List<CommentEntity>, commits: List<CommitEntity>, filesModified: List<FileEntity>, initBranch: String, finalBranch: String) : this() {
        this.project = project
        this.title = title
        this.status = status
        this.creationDate = creationDate
        this.mergeDate = mergeDate
        this.closeDate = closeDate
        this.creatorDev = creatorDev
        this.mergeDev = mergeDev
        this.comments = comments
        this.commits = commits
        this.filesModified = filesModified
        this.initBranch = initBranch
        this.finalBranch = finalBranch
    }

    companion object {
        fun fromDto(pullRequest: PullRequest) {
        }
    }
}
