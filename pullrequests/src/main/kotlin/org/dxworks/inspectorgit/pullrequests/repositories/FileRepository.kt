package org.dxworks.inspectorgit.pullrequests.repositories

import org.dxworks.inspectorgit.pullrequests.entities.FileEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FileRepository : CrudRepository<FileEntity, Long>
