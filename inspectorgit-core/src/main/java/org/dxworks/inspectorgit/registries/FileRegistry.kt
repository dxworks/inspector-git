package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.File

class FileRegistry : AbstractRegistry<File, String>() {
    override fun getID(entity: File): String {
        return entity.fullyQualifiedName
    }
}
