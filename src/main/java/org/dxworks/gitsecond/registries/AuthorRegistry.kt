package org.dxworks.gitsecond.registries

import org.dxworks.gitsecond.model.Author
import org.dxworks.gitsecond.model.AuthorID

class AuthorRegistry : AbstractRegistry<Author, AuthorID>() {
    override fun getID(entity: Author): AuthorID {
        return entity.id
    }
}
