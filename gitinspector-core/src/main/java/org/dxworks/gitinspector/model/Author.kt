package org.dxworks.gitinspector.model

data class Author(var id: AuthorID, var commits: MutableList<Commit> = ArrayList()) {
    var parent: Author? = null
    var children: MutableList<Author> = ArrayList()

    val allCommits: List<Commit>
        get() = commits + children.flatMap { it.commits }

    val allChanges: List<Change>
        get() = allCommits.flatMap { it.changes }

    val allFiles: List<File>
        get() = allChanges.map { it.file }
}
