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
    commit.changes = if (commit.isMergeCommit)
        getMergeCommitChanges(commit, changeSets, project)
    else
        changeSets.flatMap { it.changes }
                .map {
                    val annotatedLines = it.annotatedLines.map { line -> AnnotatedLine(commit, line.lineNumber, line.content) }.toMutableList()
                    val change = Change(commit = commit,
                            type = transformChangeType(it.type),
                            file = getFileForChange(it, project),
                            oldFilename = it.oldFileName,
                            newFileName = it.newFileName,
                            lineChanges = DiffParser(it.diff).lineChanges,
                            annotatedLines = annotatedLines)
                    change.file.changes.add(change)
                    change
                }
}

fun getMergeCommitChanges(commit: Commit, changeSets: List<ChangesData>, project: Project): List<Change> {
    return changeSets.flatMap { it.changes }.map { changeData ->
        //        val removeChanges: List<LineChange>
//        val addChanges: List<LineChange>
//        val file = getFileForChange(changeData, project)
//        val last = file.changes.last()
//        val annotatedLines = changeData.annotatedLines.map { line -> AnnotatedLine(project.commitRegistry.getByID(line.commitId)!!, line.lineNumber, line.content) }
//        removeChanges = last.annotatedLines.filter { !annotatedLines.contains(it) }
//                .map { LineChange(LineOperation.REMOVE, it.lineNumber, it.content) }
//
//        addChanges = annotatedLines.filter { last.annotatedLines.contains(it) }
//                .map { LineChange(LineOperation.ADD, it.lineNumber, it.content) }
//        val lineChanges: MutableList<LineChange> = ArrayList()
//        lineChanges.addAll(removeChanges)
//        lineChanges.addAll(addChanges)
//
        val change = Change(commit = commit,
                type = transformChangeType(changeData.type),
                file = getFileForChange(changeData, project),
                oldFilename = changeData.oldFileName,
                newFileName = changeData.newFileName,
                lineChanges = ArrayList(),
                annotatedLines = changeData.annotatedLines.map { line -> AnnotatedLine(project.commitRegistry.getByID(line.commitId)!!, line.lineNumber, line.content) }.toMutableList())
        change.file.changes.add(change)
        change
    }
}

private fun getFileForChange(changeData: ChangeData, project: Project): File {

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

    return file!!
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
