package org.dxworks.inspectorgit.registries.git

import org.dxworks.inspectorgit.model.git.File
import org.dxworks.inspectorgit.registries.AbstractRegistry
import java.util.*

class FileRegistry : AbstractRegistry<File, UUID>() {
    override fun getId(entity: File) = entity.id
}
