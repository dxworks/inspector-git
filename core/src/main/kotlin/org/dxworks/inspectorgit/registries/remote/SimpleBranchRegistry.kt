package org.dxworks.inspectorgit.registries.remote

import org.dxworks.inspectorgit.model.remote.SimpleBranch
import org.dxworks.inspectorgit.registries.AbstractRegistry

class SimpleBranchRegistry : AbstractRegistry<SimpleBranch, String>() {
    override fun getID(entity: SimpleBranch) = entity.ref
}