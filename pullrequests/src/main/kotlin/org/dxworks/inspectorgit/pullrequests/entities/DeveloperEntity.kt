package org.dxworks.inspectorgit.pullrequests.entities

import BaseEntity
import java.util.*
import javax.persistence.*


@Entity
class DeveloperEntity() : BaseEntity<UUID>(UUID.randomUUID()) {

    @ManyToOne
//        @JoinColumn(name = "projectId")
    lateinit var project: PRProjectEntity

    @Column
    lateinit var username: String

    @Column
    lateinit var name: String

    lateinit var email: String
    @OneToMany(mappedBy = "creatorDev", cascade = [CascadeType.ALL])
    lateinit var createdPRS: List<PullRequestEntity>

    @OneToMany(mappedBy = "mergeDev", cascade = [CascadeType.ALL])
    lateinit var mergedPRS: List<PullRequestEntity>

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL])
    lateinit var comments: List<CommentEntity>

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL])
    lateinit var commits: List<CommitEntity>


    constructor(project: PRProjectEntity, username: String, name: String, email: String, createdPRS: List<PullRequestEntity>, mergedPRS: List<PullRequestEntity>, comments: List<CommentEntity>, commits: List<CommitEntity>) : this() {
        this.project = project
        this.username = username
        this.name = name
        this.email = email
        this.createdPRS = createdPRS
        this.mergedPRS = mergedPRS
        this.comments = comments
        this.commits = commits
    }
}
