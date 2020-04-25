package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.Developer

class DeveloperRegistry : AbstractRegistry<Developer, String>() {
    override fun getID(entity: Developer) = entity.name

}