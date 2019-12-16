package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.dto.SystemDTO

interface SystemService {
    fun create(systemDTO: SystemDTO)
}