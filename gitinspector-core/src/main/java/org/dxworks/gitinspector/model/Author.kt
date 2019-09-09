package org.dxworks.gitinspector.model

data class Author(var id: AuthorID, var commits: MutableList<Commit>)
