package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.git.GitAccount
import org.dxworks.inspectorgit.model.git.GitAccountId

class GitAccountRegistry : AbstractRegistry<GitAccount, GitAccountId>() {
    override fun getID(entity: GitAccount): GitAccountId {
        return entity.gitId
    }
}
