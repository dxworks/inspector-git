package org.dxworks.inspectorgit.pullrequests.repositories

import org.dxworks.inspectorgit.pullrequests.entities.DeveloperEntity
import org.dxworks.inspectorgit.pullrequests.entities.PRProjectEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeveloperRepository : CrudRepository<DeveloperEntity, Long> {

    fun findById(id: Long?): DeveloperEntity

    fun findAllByProject(project: PRProjectEntity): List<DeveloperEntity>
}

