package org.dxworks.inspectorgit.persistency.repositories

import org.dxworks.inspectorgit.persistency.entities.ProjectEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectRepository : CrudRepository<ProjectEntity, UUID>