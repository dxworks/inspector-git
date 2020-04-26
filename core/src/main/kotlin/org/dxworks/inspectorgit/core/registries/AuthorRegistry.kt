package org.dxworks.inspectorgit.core.registries

import org.dxworks.inspectorgit.core.model.Author
import org.dxworks.inspectorgit.core.model.AuthorId

class AuthorRegistry : AbstractRegistry<Author, AuthorId>() {
    override fun getID(entity: Author): AuthorId {
        return entity.id
    }
}
