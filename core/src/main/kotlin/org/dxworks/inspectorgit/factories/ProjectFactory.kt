package org.dxworks.inspectorgit.factories

import org.dxworks.inspectorgit.model.Project

interface ProjectFactory {
    fun create(dto: Any, name: String): Project?
}