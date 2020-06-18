package org.dxworks.inspectorgit.remote.repositories

import org.dxworks.inspectorgit.remote.entities.PRProjectEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PRProjectRepository : CrudRepository<PRProjectEntity, Long> {

    fun findById(id: Long?): PRProjectEntity
    fun findByRepository(repositoryName: String): PRProjectEntity
}
