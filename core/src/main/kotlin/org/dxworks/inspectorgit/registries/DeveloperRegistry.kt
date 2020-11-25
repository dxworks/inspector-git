package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.Developer

class DeveloperRegistry : AbstractRegistry<Developer, String>() {
    override fun getId(entity: Developer) = entity.name
    fun clear() {
        map.clear()
    }
}
