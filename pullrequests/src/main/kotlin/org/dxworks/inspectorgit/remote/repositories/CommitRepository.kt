package org.dxworks.inspectorgit.remote.repositories

import org.dxworks.inspectorgit.remote.entities.CommitEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommitRepository : CrudRepository<CommitEntity, Long>
