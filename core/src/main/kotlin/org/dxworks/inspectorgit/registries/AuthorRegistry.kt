package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.Author
import org.dxworks.inspectorgit.model.AuthorId

class AuthorRegistry : AbstractRegistry<Author, AuthorId>() {
    override fun getID(entity: Author): AuthorId {
        return entity.id
    }
}
