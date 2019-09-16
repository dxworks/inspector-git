package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.Author
import org.dxworks.inspectorgit.model.AuthorID

class AuthorRegistry : AbstractRegistry<Author, AuthorID>() {
    override fun getID(entity: Author): AuthorID {
        return entity.id
    }
}
