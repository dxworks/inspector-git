package org.dxworks.gitinspector.registries

import org.dxworks.gitinspector.model.File

class FileRegistry : AbstractRegistry<File, String>() {
    override fun getID(entity: File): String {
        return entity.fullyQualifiedName
    }
}
