package org.dxworks.gitinspector.registries

import org.dxworks.gitinspector.model.Author
import org.dxworks.gitinspector.model.AuthorID

class AuthorRegistry : AbstractRegistry<Author, AuthorID>() {
    override fun getID(entity: Author): AuthorID {
        return entity.id
    }
}
