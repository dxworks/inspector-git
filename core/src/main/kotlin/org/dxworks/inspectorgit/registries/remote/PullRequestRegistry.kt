package org.dxworks.inspectorgit.registries.remote

import org.dxworks.inspectorgit.model.remote.PullRequest
import org.dxworks.inspectorgit.registries.AbstractRegistry

class PullRequestRegistry : AbstractRegistry<PullRequest, Number>() {
    override fun getId(entity: PullRequest) = entity.id
}
