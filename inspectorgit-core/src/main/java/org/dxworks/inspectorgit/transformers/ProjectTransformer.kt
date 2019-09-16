package org.dxworks.inspectorgit.transformers

import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.CommitDTO
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.model.*
import org.slf4j.LoggerFactory

@Slf4j
class ProjectTransformer() {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)

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
                val change = Change(
                        commit = commit,
                        type = changeDTO.type,
                        file = getFileForChange(changeDTO, project),
                        otherCommit = if (changeDTO.otherCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.otherCommitId }!!,
                        oldFilename = changeDTO.oldFileName,
                        newFileName = changeDTO.newFileName,
                        lineChanges = getLineChanges(changeDTO),
                        annotatedLines = getAnnotatedLines(changeDTO, project))
                change.file.changes.add(change)
                LOG.info("Change created")
                change
            }
        }

        private fun getLineChanges(changeDTO: ChangeDTO) =
                changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, it.lineNumber, it.content) }.toMutableList()

        private fun getAnnotatedLines(changeDTO: ChangeDTO, project: Project): MutableList<AnnotatedLine> {
            LOG.info("Calculating annotated lines")
            return changeDTO.annotatedLines.map { AnnotatedLine(project.commitRegistry.getByID(it.commitId)!!, it.number, it.content) }.toMutableList()
        }

        private fun getFileForChange(change: ChangeDTO, project: Project): File {
            LOG.info("Getting file")
            return when (change.type) {
                ChangeType.ADD -> {
                    var file = project.fileRegistry.getByID(change.newFileName)
                    if (file == null) {
                        file = File(change.newFileName, change.isBinary)
                        project.fileRegistry.add(file)
                    }
                    file
                }
                ChangeType.RENAME -> {
                    val file = project.fileRegistry.getByID(change.oldFileName)
                    if (file == null) {
                        System.err.println("File not found for rename change: $change")
                    } else {
                        file.fullyQualifiedName = change.newFileName
                        project.fileRegistry.add(file)
                    }
                    file!!
                }
                ChangeType.DELETE -> {
                    val file = project.fileRegistry.getByID(change.oldFileName)
                    if (file == null)
                        System.err.println("File not found for change: $change")
                    file!!
                }
                else -> {
                    val file = project.fileRegistry.getByID(change.newFileName)
                    if (file == null) {
                        System.err.println("File not found for change: $change")
                    }
                    file!!
                }
            }
        }

        private fun getCommitAuthor(commitDTO: CommitDTO, project: Project): Author {
            val authorID = AuthorID(commitDTO.authorEmail, commitDTO.authorName)

            var author = project.authorRegistry.getByID(authorID)
            if (author == null) {
                author = Author(authorID)
                project.authorRegistry.add(author)
            }

            return author
        }

        private fun getParentFromIds(parentIds: List<String>, project: Project): List<Commit> {
            return parentIds.mapNotNull { project.commitRegistry.getByID(it) }
        }
    }
}