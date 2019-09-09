package org.dxworks.gitinspector.model

data class AuthorID(var email: String, var name: String) {
    override fun toString(): String {
        return "$name <$email>"
    }
}
