package org.dxworks.inspectorgit.pullrequests.entities

import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany

@Entity
class PRProjectEntity(
        @Id
        val id: Long,

        val repository: String,

        val owner: String,

        @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
        val developers: List<DeveloperEntity>,

        @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL])
        val pullRequests: List<PullRequestEntity>
)
