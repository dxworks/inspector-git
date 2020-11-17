package org.dxworks.inspectorgit.model

abstract class Account(
        val name: String,
        val project: Project,
        var developer: Developer? = null
) {
    abstract val id: String
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Account

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
