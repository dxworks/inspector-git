package org.dxworks.inspectorgit.model.git

import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Project

class GitAccount(
        var gitId: GitAccountId,
        project: Project,
        var commits: List<Commit> = emptyList()
) : Account(
        gitId.name,
        project
) {
    override val id: String
        get() = gitId.toString()

    val changes: List<Change>
        get() = commits.flatMap { it.changes }

    val files: List<File>
        get() = changes.map { it.file }.distinct()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GitAccount

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
