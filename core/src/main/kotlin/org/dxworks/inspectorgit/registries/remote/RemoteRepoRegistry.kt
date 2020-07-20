package org.dxworks.inspectorgit.registries.remote

import org.dxworks.inspectorgit.model.remote.RemoteRepo
import org.dxworks.inspectorgit.registries.AbstractRegistry

class RemoteRepoRegistry : AbstractRegistry<RemoteRepo, Number>() {
    override fun getId(entity: RemoteRepo) = entity.id
}