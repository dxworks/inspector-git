package org.dxworks.gitsecond.transformers

import org.dxworks.gitsecond.data.ChangeData
import org.dxworks.gitsecond.data.ChangesData
import org.dxworks.gitsecond.data.CommitData
import org.dxworks.gitsecond.model.*
import org.eclipse.jgit.diff.DiffEntry

fun createProject(commitDatas: List<CommitData>, projectId: String): Project {
    val project = Project(projectId)

    commitDatas.forEach {
        val author = getCommitAuthor(it, project)

        val commit = Commit(id = it.id,
                message = it.message,
                date = it.date,
                author = author,
                parents = getParentFromIds(it.parentIds, project),
                changes = ArrayList())

        addChangesToCommit(it.changeSets, commit, project)
        author.commits.add(commit)
        project.commitRegistry.add(commit)
    }

    return project
}

private fun addChangesToCommit(changeSets: List<ChangesData>, commit: Commit, project: Project) {
    commit.changes = changeSets
            .flatMap { it.changes }
            .map {
                val change = Change(commit = commit,
                        type = transformChangeType(it.type),
                        file = getFileForChange(it, project),
                        lines = ArrayList(), //getLinesFromDiff(it.diff),
                        oldFilename = it.oldFileName,
                        newFileName = it.newFileName)
                change.file?.changes?.add(change)
                change
            }
}

//private fun getLinesFromDiff(diff: String): List<Line> {
//
//}

private fun getFileForChange(changeData: ChangeData, project: Project): File? {

    val changeType = transformChangeType(changeData.type)

    var file: File?

    when (changeType) {
        ChangeType.ADD -> {
            file = project.fileRegistry.getByID(changeData.newFileName)
            if (file == null) {
                file = File(fullyQualifiedName = changeData.newFileName, changes = ArrayList())
                project.fileRegistry.add(file)
            }
        }
        ChangeType.RENAME -> {
            file = project.fileRegistry.getByID(changeData.oldFileName)
            if (file == null) {
                System.err.println("File not found for rename change: $changeData")
            } else {
                file.fullyQualifiedName = changeData.newFileName
            }
        }
        else -> {
            file = project.fileRegistry.getByID(changeData.newFileName)
            if (file == null) {
                System.err.println("File not found for change: $changeData")
            }
        }
    }

    return file
}

private fun transformChangeType(type: DiffEntry.ChangeType): ChangeType {
    return when (type) {
        DiffEntry.ChangeType.ADD -> ChangeType.ADD
        DiffEntry.ChangeType.COPY -> ChangeType.COPY
        DiffEntry.ChangeType.DELETE -> ChangeType.DELETE
        DiffEntry.ChangeType.MODIFY -> ChangeType.MODIFY
        DiffEntry.ChangeType.RENAME -> ChangeType.RENAME
    }
}

private fun getCommitAuthor(commitData: CommitData, project: Project): Author {
    val authorID = AuthorID(name = commitData.authorName, email = commitData.authorEmail)

    var author = project.authorRegistry.getByID(authorID)
    if (author == null) {
        author = Author(id = authorID, commits = ArrayList())
        project.authorRegistry.add(author)
    }

    return author
}

private fun getParentFromIds(parentIds: List<String>, project: Project): List<Commit> {
    return parentIds.map { project.commitRegistry.getByID(it) }.filterNotNull()
}
