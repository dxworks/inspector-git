package org.dxworks.inspectorgit.model.git

data class GitAccountId(var email: String, var name: String) {
    override fun toString(): String {
        return "$name <$email>"
    }
}
