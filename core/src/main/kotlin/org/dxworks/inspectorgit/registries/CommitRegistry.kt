package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.git.Commit

class CommitRegistry : AbstractRegistry<Commit, String>() {

    override fun getById(id: String): Commit? {
        return if (id.startsWith("^")) findByPrefix(id.removePrefix("^")) else super.getById(id)
    }

    override fun contains(id: String): Boolean {
        return if (id.startsWith("^")) findByPrefix(id.removePrefix("^")) != null else super.contains(id)
    }

    private fun findByPrefix(prefix: String) = all.find { it.id.startsWith(prefix) }

    override fun getID(entity: Commit): String {
        return entity.id
    }
}
