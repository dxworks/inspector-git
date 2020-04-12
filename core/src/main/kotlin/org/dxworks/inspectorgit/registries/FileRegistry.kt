package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.File
import java.util.*

class FileRegistry : AbstractRegistry<File, UUID>() {
    override fun getID(entity: File) = entity.id
}
