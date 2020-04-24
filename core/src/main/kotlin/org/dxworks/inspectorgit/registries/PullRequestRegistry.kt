package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.pr.PullRequest

class PullRequestRegistry : AbstractRegistry<PullRequest, String>() {
    override fun getID(entity: PullRequest) = entity.id
}