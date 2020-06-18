package org.dxworks.inspectorgit.registries.pr

import org.dxworks.inspectorgit.model.remote.RemoteRepo
import org.dxworks.inspectorgit.registries.AbstractRegistry

class RemoteRepoRegistry : AbstractRegistry<RemoteRepo, Number>() {
    override fun getID(entity: RemoteRepo) = entity.id
}