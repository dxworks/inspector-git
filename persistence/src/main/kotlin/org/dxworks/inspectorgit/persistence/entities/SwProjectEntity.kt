package org.dxworks.inspectorgit.persistence.entities

import javax.persistence.*

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["name", "platform"])])
class SwProjectEntity : ProjectEntity() {
    @Column
    var description: String? = null

    @Column(unique = true)
    var path: String? = null

    @Column
    var branch: String? = null

    @Column
    val repositoryHttpUrl: String? = null

    @Column
    var pullRequestsEnabled: Boolean = false

    @Lob
    var gitLogDtoString: String? = null
}
