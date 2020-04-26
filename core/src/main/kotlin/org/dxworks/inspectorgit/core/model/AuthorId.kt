package org.dxworks.inspectorgit.core.model

data class AuthorId(
        var email: String,
        var name: String
) {
    override fun toString(): String {
        return "$name <$email>"
    }
}
