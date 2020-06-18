package org.dxworks.inspectorgit.remote.repositories

import org.dxworks.inspectorgit.remote.entities.PRProjectEntity
import org.dxworks.inspectorgit.remote.entities.PullRequestEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PullRequestRepository : CrudRepository<PullRequestEntity, Long> {

    fun findById(id: Long?): PullRequestEntity
    fun findAllByProject(projectData: PRProjectEntity): List<PullRequestEntity>
}
