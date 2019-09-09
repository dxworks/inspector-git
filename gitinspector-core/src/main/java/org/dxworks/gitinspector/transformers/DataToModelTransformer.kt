package org.dxworks.gitinspector.transformers

import lombok.extern.slf4j.Slf4j
import org.dxworks.gitinspector.dto.ChangeDTO
import org.dxworks.gitinspector.dto.CommitDTO
import org.dxworks.gitinspector.dto.ProjectDTO
import org.dxworks.gitinspector.enums.ChangeType
import org.dxworks.gitinspector.model.AnnotatedLine
import org.dxworks.gitinspector.model.LineChange
import org.dxworks.gitinspector.model.Project
import org.dxworks.gitinspector.model.*
import org.slf4j.LoggerFactory

@Slf4j
class OptimusSecond() {
    companion object {
        private val LOG = LoggerFactory.getLogger(OptimusSecond::class.java)

        fun createProject(projectDTO: ProjectDTO, projectId: String): Project {
            val project = Project(projectId)

            projectDTO.commits.forEach {
                LOG.info("Creating commit with id: ${it.id}")

                val author = getCommitAuthor(it, project)
                val commit = Commit(id = it.id,
                        message = it.message,
                        date = it.date,
                        author = author,
                        parents = getParentFromIds(it.parentIds, project),
                        changes = ArrayList())

                project.commitRegistry.add(commit)
                author.commits.add(commit)
                addChangesToCommit(it.changes, commit, project)

                LOG.info("Done creating commit with id: ${it.id}")
            }

            return project
        }

        private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: Project) {
            commit.changes = changes.map { changeDTO ->
                LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
                val change = Change(commit = commit,
                        type = changeDTO.type,
                        file = getFileForChange(changeDTO, project),
                        oldFilename = changeDTO.oldFileName,
                        newFileName = changeDTO.newFileName,
                        lineChanges = changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, it.lineNumber, it.content) }.toMutableList(),
                        annotatedLines = changeDTO.annotatedLines.map { AnnotatedLine(project.commitRegistry.getByID(it.commitId)!!, it.number, it.content) }.toMutableList())
                change.file.addChange(change)
                LOG.info("Change created")
                change
            }
        }

        private fun getFileForChange(change: ChangeDTO, project: Project): File {
            LOG.info("Getting file")
            val changeType = change.type

            var file: File?

            when (changeType) {
                ChangeType.ADD -> {
                    file = project.fileRegistry.getByID(change.newFileName)
                    if (file == null) {
                        file = File(fullyQualifiedName = change.newFileName)
                        project.fileRegistry.add(file)
                    }
                }
                ChangeType.RENAME -> {
                    file = project.fileRegistry.getByID(change.oldFileName)
                    if (file == null) {
                        System.err.println("File not found for rename change: $change")
                    } else {
                        file.fullyQualifiedName = change.newFileName
                        project.fileRegistry.add(file)
                    }
                }
                ChangeType.DELETE -> {
                    file = project.fileRegistry.getByID(change.oldFileName)
                    if (file == null) {
                        System.err.println("File not found for change: $change")
                    } else {
                        file.isAlive = false
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
    }
}