package org.dxworks.inspectorgit.remote.repositories

import org.dxworks.inspectorgit.remote.entities.DeveloperEntity
import org.dxworks.inspectorgit.remote.entities.PRProjectEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DeveloperRepository : CrudRepository<DeveloperEntity, Long> {

    fun findById(id: Long?): DeveloperEntity

    fun findAllByProject(project: PRProjectEntity): List<DeveloperEntity>
}

