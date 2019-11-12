package org.dxworks.inspectorgit.pullrequests.repositories

import org.dxworks.inspectorgit.pullrequests.entities.CommentEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CrudRepository<CommentEntity, Long>
