package org.dxworks.inspectorgit.transformers

import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.dto.ChangeDTO
import org.dxworks.inspectorgit.dto.CommitDTO
import org.dxworks.inspectorgit.dto.LineChangeDTO
import org.dxworks.inspectorgit.dto.ProjectDTO
import org.dxworks.inspectorgit.enums.ChangeType
import org.dxworks.inspectorgit.enums.LineOperation
import org.dxworks.inspectorgit.model.*
import org.dxworks.inspectorgit.registries.FileRegistry
import org.slf4j.LoggerFactory

@Slf4j
class ProjectTransformer {
    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectTransformer::class.java)

        fun createProject(projectDTO: ProjectDTO, projectId: String): Project {
            val project = Project(projectId)

            projectDTO.commits.forEach {
                LOG.info("Creating commit with id: ${it.id}")

                val author = getAuthor(it, project)
                LOG.info("Parsed author")
                val committer = getCommitter(it, project)
                LOG.info("Parsed committer")

                val commit = Commit(id = it.id,
                        message = it.message,
                        authorDate = it.authorDate,
                        committerDate = it.committerDate,
                        author = author,
                        committer = committer,
                        parents = getParentFromIds(it.parentIds, project),
                        changes = ArrayList())

                LOG.info("Adding commit to registry and authors")
                project.commitRegistry.add(commit)
                author.commits.add(commit)
                if (committer != author)
                    committer.commits.add(commit)

                addChangesToCommit(it.changes, commit, project)

                LOG.info("Done creating commit with id: ${it.id}")
            }

            return project
        }

        private fun addChangesToCommit(changes: List<ChangeDTO>, commit: Commit, project: Project) {
            LOG.info("Filtering changes")
            val admittedChanges = if (commit.isMergeCommit) filterChanges(changes, project) else changes
            LOG.info("Done filtering changes")
            commit.changes = admittedChanges.map { changeDTO ->
                LOG.info("Creating ${changeDTO.type} change for file: ${changeDTO.oldFileName} -> ${changeDTO.newFileName}")
                val annotatedLines = getAnnotatedLines(changeDTO, project)
                val file = getFileForChange(changeDTO, project)
                val parentCommit = if (changeDTO.parentCommitId.isEmpty()) null else commit.parents.find { it.id == changeDTO.parentCommitId }!!
                val change = Change(
                        commit = commit,
                        type = changeDTO.type,
                        file = file,
                        parentCommit = parentCommit,
                        oldFileName = changeDTO.oldFileName,
                        newFileName = changeDTO.newFileName,
                        lineChanges = getLineChanges(changeDTO, annotatedLines, commit, file, parentCommit),
                        annotatedLines = annotatedLines)
                change.file.changes.add(change)
                LOG.info("Change created")
                change
            }
        }

        private fun filterChanges(changes: List<ChangeDTO>, project: Project): List<ChangeDTO> {
            LOG.info("Getting potentially renamed files")
            val potentiallyRenamedFiles = changes.filter { it.type == ChangeType.ADD }
                    .mapNotNull { project.fileRegistry.getByID(it.newFileName) }
            LOG.info("Found: ${potentiallyRenamedFiles.size}")
            LOG.info("Getting rename changes")
            val renameChanges = potentiallyRenamedFiles.mapNotNull { it.changes.find { change -> change.isRenameChange } }
            val newFileNames = renameChanges.map { it.newFileName }
            val oldFileNames = renameChanges.map { it.oldFileName }
            LOG.info("Found: ${renameChanges.size}")
            LOG.info("filtering legit changes")
            val legitChanges = changes.filter { !((it.type == ChangeType.ADD && newFileNames.contains(it.newFileName)) || (it.type == ChangeType.DELETE && oldFileNames.contains(it.oldFileName))) }
            val fakeAddChanges = changes.filter { (it.type == ChangeType.ADD && newFileNames.contains(it.newFileName)) }
            LOG.info("Found: ${legitChanges.size}")
            LOG.info("Substituting ${fakeAddChanges.size} add-delete pairs with rename changes")
            return legitChanges + fakeAddChanges.map {
                val renameChange = renameChanges.find { rc -> rc.newFileName == it.newFileName }!!
                ChangeDTO(oldFileName = renameChange.oldFileName,
                        newFileName = renameChange.newFileName,
                        type = ChangeType.RENAME,
                        parentCommitId = it.parentCommitId,
                        isBinary = it.isBinary,
                        hunks = emptyList(),
                        annotatedLines = it.annotatedLines,
                        isBlame = it.isBlame
                )
            }
        }

        private fun getLineChanges(changeDTO: ChangeDTO, annotatedLines: MutableList<AnnotatedLine>, commit: Commit, file: File, parentCommit: Commit?): MutableList<LineChange> {
            return changeDTO.hunks.flatMap { it.lineChanges }.map { LineChange(it.operation, it.number, getAnnotatedLine(it, annotatedLines, commit, file, parentCommit)) }.toMutableList()
        }

        private fun getAnnotatedLine(lineChangeDTO: LineChangeDTO, annotatedLines: MutableList<AnnotatedLine>, commit: Commit, file: File, parentCommit: Commit?): AnnotatedLine {
            return if (lineChangeDTO.operation == LineOperation.ADD)
                annotatedLines.getOrElse(lineChangeDTO.number) {
                    AnnotatedLine(commit, lineChangeDTO.number, lineChangeDTO.content)
                }
            else file.getLastChange(parentCommit)!!.annotatedLines[lineChangeDTO.number - 1]
        }

        private fun getAnnotatedLines(changeDTO: ChangeDTO, project: Project): MutableList<AnnotatedLine> {
            LOG.info("Calculating annotated lines")
            return changeDTO.annotatedLines.map { AnnotatedLine(project.commitRegistry.getByID(it.commitId)!!, it.number, it.content) }.toMutableList()
        }

        private fun getFileForChange(change: ChangeDTO, project: Project): File {
            LOG.info("Getting file")
            return when (change.type) {
                ChangeType.ADD -> {
                    val file = File(change.isBinary)
                    project.fileRegistry.add(file, change.newFileName)
                    file
                }
                ChangeType.RENAME -> {
                    val file = getFileForChange(change.oldFileName, project.commitRegistry.getByID(change.parentCommitId)!!, project.fileRegistry)
                    project.fileRegistry.add(file, change.newFileName)
                    file
                }
                else -> {
                    getFileForChange(change.oldFileName, project.commitRegistry.getByID(change.parentCommitId)!!, project.fileRegistry)
                }
            }
        }

        private tailrec fun getFileForChange(name: String, commit: Commit?, fileRegistry: FileRegistry): File {
            if (commit == null)
                return fileRegistry.getByID(name)!!
            val change = commit.changes.find { it.newFileName == name }
            return change?.file ?: getFileForChange(name, commit.parents.firstOrNull(), fileRegistry)
        }

        private fun getAuthor(commitDTO: CommitDTO, project: Project): Author {
            return getAuthor(project, AuthorID(commitDTO.authorEmail, commitDTO.authorName))
        }

        private fun getCommitter(commitDTO: CommitDTO, project: Project): Author {
            return getAuthor(project, AuthorID(commitDTO.committerEmail, commitDTO.committerName))
        }

        private fun getAuthor(project: Project, authorID: AuthorID): Author {
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
