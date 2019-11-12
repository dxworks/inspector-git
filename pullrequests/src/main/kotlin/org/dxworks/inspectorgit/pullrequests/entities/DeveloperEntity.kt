package org.dxworks.inspectorgit.pullrequests.entities

import javax.persistence.*


@Entity
class DeveloperEntity(
        @Id
        val id: Long,

        @ManyToOne
        @JoinColumn(name = "projectId")
        val project: PRProjectEntity,

        @Column
        val username: String,

        @Column
        val name: String,

        val email: String,
        @OneToMany(mappedBy = "creatorDev", cascade = [CascadeType.ALL])
        val createdPRS: List<PullRequestEntity>,

        @OneToMany(mappedBy = "mergeDev", cascade = [CascadeType.ALL])
        val mergedPRS: List<PullRequestEntity>,

        @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL])
        val comments: List<CommentEntity>,

        @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL])
        val commits: List<CommitEntity>
)