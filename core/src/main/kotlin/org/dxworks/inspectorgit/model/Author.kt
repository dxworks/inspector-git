package org.dxworks.inspectorgit.model

data class Author(var id: AuthorId, var commits: MutableList<Commit> = ArrayList()) {
    var parent: Author? = null
    var children: MutableList<Author> = ArrayList()

    val allCommits: List<Commit>
        get() = commits + children.flatMap { it.commits }

    val allChanges: List<Change>
        get() = allCommits.flatMap { it.changes }

    val allFiles: List<File>
        get() = allChanges.map { it.file }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Author

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
