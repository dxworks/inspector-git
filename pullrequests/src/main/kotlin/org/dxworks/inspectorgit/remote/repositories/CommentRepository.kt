package org.dxworks.inspectorgit.remote.repositories

import org.dxworks.inspectorgit.remote.entities.CommentEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : CrudRepository<CommentEntity, Long>
