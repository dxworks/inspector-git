package org.dxworks.inspectorgit.pullrequests.repositories

import org.dxworks.inspectorgit.pullrequests.entities.CommitEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommitRepository : CrudRepository<CommitEntity, Long>
