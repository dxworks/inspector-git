package org.dxworks.inspectorgit.pullrequests.repositories

import org.dxworks.inspectorgit.pullrequests.entities.PRProjectEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PRProjectRepository : CrudRepository<PRProjectEntity, Long> {

    fun findById(id: Long?): PRProjectEntity
    fun findByRepository(repositoryName: String): PRProjectEntity
}
