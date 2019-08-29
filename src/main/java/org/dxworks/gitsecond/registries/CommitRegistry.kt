package org.dxworks.gitsecond.registries

import org.dxworks.gitsecond.model.Commit

class CommitRegistry : AbstractRegistry<Commit, String>() {
    override fun getID(entity: Commit): String {
        return entity.id
    }
}
