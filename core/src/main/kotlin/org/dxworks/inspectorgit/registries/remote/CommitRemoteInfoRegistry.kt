package org.dxworks.inspectorgit.registries.remote

import org.dxworks.inspectorgit.model.remote.CommitRemoteInfo
import org.dxworks.inspectorgit.registries.AbstractRegistry

class CommitRemoteInfoRegistry : AbstractRegistry<CommitRemoteInfo, String>() {
    override fun getId(entity: CommitRemoteInfo) = entity.commitId

}
