package org.dxworks.inspectorgit.core.registries

import org.dxworks.inspectorgit.core.model.File
import java.util.*

class FileRegistry : AbstractRegistry<File, UUID>() {
    override fun getID(entity: File) = entity.id
}
