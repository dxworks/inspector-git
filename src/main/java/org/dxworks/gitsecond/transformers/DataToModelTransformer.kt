package org.dxworks.gitsecond.transformers

import org.dxworks.dto.ChangeDTO
import org.dxworks.dto.CommitDTO
import org.dxworks.gitsecond.model.*

fun createProject(commitDtos: List<CommitDTO>, projectId: String): Project {
    val project = Project(projectId)

    commitDtos.forEach {
        val author = getCommitAuthor(it, project)

        val commit = Commit(id = it.id,
                message = it.message,
                date = it.date,
                author = author,
                parents = getParentFromIds(it.parentIds, project),
                changes = ArrayList())

        addChangesToCommit(it.changes, commit, project)
        author.commits.add(commit)
        project.commitRegistry.add(commit)
    }

    return project
}

private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: Project) {
    commit.changes = if (commit.isMergeCommit)
        getMergeCommitChanges(commit, changes, project)
    else
        changes.map { changeDTO ->
            val change = Change(commit = commit,
                    type = changeDTO.type,
                    file = getFileForChange(changeDTO, project),
                    oldFilename = changeDTO.oldFileName,
                    newFileName = changeDTO.newFileName,
                    lineChanges = changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, it.lineNumber, it.content) }.toMutableList(),
                    annotatedLines = changeDTO.annotatedLines.map { AnnotatedLine(commit, it.number, it.content) }.toMutableList())
            change.file.changes.add(change)
            change
        }
}

fun getMergeCommitChanges(commit: Commit, changes: List<ChangeDTO>, project: Project): List<Change> {
    return changes.map { changeDTO ->
        val change = Change(commit = commit,
                type = changeDTO.type,
                file = getFileForChange(changeDTO, project),
                oldFilename = changeDTO.oldFileName,
                newFileName = changeDTO.newFileName,
                lineChanges = ArrayList(),
                annotatedLines = changeDTO.annotatedLines.map { AnnotatedLine(project.commitRegistry.getByID(it.commitId)!!, it.number, it.content) }.toMutableList())
        change.file.changes.add(change)
        change
    }
}

private fun getFileForChange(change: ChangeDTO, project: Project): File {

    val changeType = change.type

    var file: File?

    when (changeType) {
        ChangeType.ADD -> {
            file = project.fileRegistry.getByID(change.newFileName)
            if (file == null) {
                file = File(fullyQualifiedName = change.newFileName, changes = ArrayList())
                project.fileRegistry.add(file)
            }
        }
        ChangeType.RENAME -> {
            file = project.fileRegistry.getByID(change.oldFileName)
            if (file == null) {
                System.err.println("File not found for rename change: $change")
            } else {
                file.fullyQualifiedName = change.newFileName
            }
        }
        else -> {
            file = project.fileRegistry.getByID(change.newFileName)
            if (file == null) {
                System.err.println("File not found for change: $change")
            }
        }
    }

    return file!!
}

private fun getCommitAuthor(commitDTO: CommitDTO, project: Project): Author {
    val authorID = AuthorID(name = commitDTO.authorName, email = commitDTO.authorEmail)

    var author = project.authorRegistry.getByID(authorID)
    if (author == null) {
        author = Author(id = authorID, commits = ArrayList())
        project.authorRegistry.add(author)
    }

    return author
}

private fun getParentFromIds(parentIds: List<String>, project: Project): List<Commit> {
    return parentIds.mapNotNull { project.commitRegistry.getByID(it) }
}
