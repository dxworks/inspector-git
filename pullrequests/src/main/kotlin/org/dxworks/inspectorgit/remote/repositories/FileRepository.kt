package org.dxworks.inspectorgit.remote.repositories

import org.dxworks.inspectorgit.remote.entities.FileEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : CrudRepository<FileEntity, Long>
