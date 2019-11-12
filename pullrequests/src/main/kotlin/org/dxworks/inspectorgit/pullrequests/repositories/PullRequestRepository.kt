package org.dxworks.inspectorgit.pullrequests.repositories

import org.dxworks.inspectorgit.pullrequests.entities.PRProjectEntity
import org.dxworks.inspectorgit.pullrequests.entities.PullRequestEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PullRequestRepository : CrudRepository<PullRequestEntity, Long> {

    fun findById(id: Long?): PullRequestEntity
    fun findAllByProject(projectData: PRProjectEntity): List<PullRequestEntity>
}
