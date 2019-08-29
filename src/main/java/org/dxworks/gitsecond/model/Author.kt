package org.dxworks.gitsecond.model

data class Author(var id: AuthorID, var commits: MutableList<Commit>)
