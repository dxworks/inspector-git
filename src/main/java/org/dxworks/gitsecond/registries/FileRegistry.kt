package org.dxworks.gitsecond.registries

import org.dxworks.gitsecond.model.File

class FileRegistry : AbstractRegistry<File, String>() {
    override fun getID(entity: File): String {
        return entity.fullyQualifiedName
    }
}
