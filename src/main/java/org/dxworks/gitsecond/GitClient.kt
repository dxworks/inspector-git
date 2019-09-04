package org.dxworks.gitsecond

import org.dxworks.gitsecond.data.AnnotatedLineData
import org.dxworks.gitsecond.data.ChangeData
import org.dxworks.gitsecond.data.ChangesData
import org.dxworks.gitsecond.data.CommitData
import org.eclipse.jgit.api.BlameCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.revwalk.filter.RevFilter
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.treewalk.AbstractTreeIterator
import org.eclipse.jgit.treewalk.CanonicalTreeParser
import org.eclipse.jgit.treewalk.EmptyTreeIterator
import org.eclipse.jgit.treewalk.TreeWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.util.io.DisabledOutputStream
import org.slf4j.LoggerFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

class GitClient {
    private val could_not_parse_changes_correctly = "Could not parse changes correctly"

    init {
        initializeReposPath()
    }


    private fun initializeReposPath() {
        val repoFilePath = File(REPOS_PATH)
        if (!repoFilePath.exists()) {
            val didMakeDirs = repoFilePath.mkdirs()
            if (!didMakeDirs) {
                log.error("Could not make directory $REPOS_PATH")
            }
        }

        log.info("Repos path set to $REPOS_PATH")
    }

    @Throws(IOException::class)
    private fun getRepositoryByProjectIdAndRepoName(repoName: String): Git {
        return Git.open(Paths.get(REPOS_PATH + File.separator + repoName + File.separator + ".git").toFile())
    }

    fun checkoutRevisionForRepo(revisionName: String, repoName: String) {
        try {
            val git = this.getRepositoryByProjectIdAndRepoName(repoName)
            git.checkout().setName(revisionName).call()
        } catch (ex: IOException) {
            log.error("could not find repository for $repoName", ex)
        } catch (ex: GitAPIException) {
            log.error("Error trying to checkout revision $revisionName for student $repoName", ex)
        }

    }

    @Throws(GitAPIException::class)
    fun cloneAndInitializeRepository(repositoryURL: String, repoName: String) {

        val studentFolder = Paths.get(REPOS_PATH + File.separator + repoName).toFile()
        if (studentFolder.exists() && studentFolder.isDirectory) {
            log.error("Student $repoName already has a cloned repository")
            return
        } else {
            val createdStudentDirectory = studentFolder.mkdirs()
            if (!createdStudentDirectory) {
                log.error("Could not create directory for student $repoName")
                return
            }
        }

        val cloneCommand = Git.cloneRepository().setURI(repositoryURL).setDirectory(studentFolder).setCloneAllBranches(true).setBranch("master").setCredentialsProvider(UsernamePasswordCredentialsProvider("mario.rivis@gmail.com", "just6and9"))
        cloneCommand.call()
    }

    @Throws(IOException::class)
    fun getFileContentForRepoAndRevision(filePath: String, repoName: String, revisionName: String): String {
        val repository = this.getRepositoryByProjectIdAndRepoName(repoName).repository
        val lastCommitId = repository.resolve(revisionName)
        val revWalk = RevWalk(repository)
        val commit = revWalk.parseCommit(lastCommitId)
        val tree = commit.tree
        val treeWalk = TreeWalk(repository)
        treeWalk.addTree(tree)
        treeWalk.isRecursive = true
        treeWalk.filter = PathFilter.create(filePath)
        if (!treeWalk.next()) {
            println("File not found")
            throw FileNotFoundException(filePath)
        } else {
            val objectId = treeWalk.getObjectId(0)
            val loader = repository.open(objectId)
            return String(loader.bytes)
        }
    }

    fun generateGitLogForDx(repoName: String): List<CommitData> {
        try {
            val git = this.getRepositoryByProjectIdAndRepoName(repoName)
            val repository = git.repository

            return git.log().setRevFilter(RevFilter.ALL).call()
                    .map { getCommitDetails(repository, it) }
        } catch (var4: GitAPIException) {
            log.error("Git Api error", var4)
        } catch (var5: IOException) {
            log.error("could not find repository $repoName", var5)
        }

        return emptyList()
    }

    private fun blame(repo: Repository, filePath: String, commitId: String): MutableList<AnnotatedLineData> {
        val gitObject = repo.resolve(commitId)
        val blameCommand = BlameCommand(repo)
        blameCommand.setFilePath(filePath)
        blameCommand.setStartCommit(gitObject)
        val blameResult = blameCommand.call()
        val rawText = blameResult.resultContents
        val length = rawText.size()

        val lines: MutableList<AnnotatedLineData> = ArrayList()

        for (i in 0 until length) {
            val commit = blameResult.getSourceCommit(i)
            val line = AnnotatedLineData(commit.name, i + 1, rawText.getString(i))
            lines.add(line)
        }

        val blameContent = String(rawText.rawContent)
        val blameLines = blameContent.split("\n")
        val blameLength = blameLines.size


//        lines.forEach {
//            println("${it.lineNumber} ${it.commit?.date} ${it.commit?.author!!.id.name} ${it.content}")
//        }

        return lines
    }

    private fun getCommitDetails(repository: Repository, revCommit: RevCommit): CommitData {
        return CommitData(id = revCommit.name,
                authorName = revCommit.authorIdent.name,
                authorEmail = revCommit.authorIdent.emailAddress,
                date = Date(revCommit.commitTime.toLong() * 1000L),
                message = revCommit.fullMessage,
                changeSets = getCommitChanges(repository, revCommit),
                parentIds = revCommit.parents.map { it.name })
    }

    private fun getCommitChanges(repository: Repository, revCommit: RevCommit): List<ChangesData> {
        val reader = repository.newObjectReader()
        val currentCommitTreeIterator = CanonicalTreeParser()

        try {
            currentCommitTreeIterator.reset(reader, revCommit.tree.id)

            return if (revCommit.parentCount == 0) {
                val parentTreeIterator = EmptyTreeIterator()
                val diffs = getDiffsBetweenCommits(repository, parentTreeIterator, currentCommitTreeIterator)
                val changes = transformDiffsToChangeDatas(revCommit, repository, diffs)
                listOf(ChangesData(null, changes))
            } else {
                val changesData = revCommit.parents
                        .map { getChangesData(repository, reader, currentCommitTreeIterator, revCommit, it) }
                        .filter { Objects.nonNull(it) }

                if (changesData.size != revCommit.parentCount) {
                    log.warn("Not all merge commit parents have been correctly parsed!")
                }
                changesData as List<ChangesData>
            }

        } catch (e: IOException) {
            log.error(could_not_parse_changes_correctly, e)
            return emptyList()
        }

    }

    private fun getChangesData(repository: Repository, reader: ObjectReader, currentCommitTreeIterator: CanonicalTreeParser, revCommit: RevCommit, parentCommit: RevCommit): ChangesData? {
        val parentIterator = CanonicalTreeParser()
        try {
            parentIterator.reset(reader, parentCommit.tree.id)
            val diffsBetweenCommits = getDiffsBetweenCommits(repository, parentIterator, currentCommitTreeIterator)
            val changeDatas = transformDiffsToChangeDatas(revCommit, repository, diffsBetweenCommits)
            cleanDataForRenames(changeDatas, revCommit, repository);

            return ChangesData(parentCommit.name, changeDatas)
        } catch (e: IOException) {
            log.error(could_not_parse_changes_correctly, e)
            return null
        } finally {
            try {
                currentCommitTreeIterator.reset(reader, revCommit.tree.id)
            } catch (e: IOException) {
                log.error(could_not_parse_changes_correctly, e)
            }

        }
    }

    private fun cleanDataForRenames(changeDatas: List<ChangeData>, revCommit: RevCommit, repository: Repository) {
        val addChangeDatas = changeDatas.filter { it.type == DiffEntry.ChangeType.ADD }
        val deleteChangeDatas = changeDatas.filter { it.type == DiffEntry.ChangeType.DELETE }

        if (addChangeDatas.isNotEmpty() && deleteChangeDatas.isNotEmpty()) {
            val git = Git(repository)
//            val log = git.log().
        }
    }

    private fun transformDiffsToChangeDatas(revCommit: RevCommit, repository: Repository, diffs: List<DiffEntry>): List<ChangeData> {
        return diffs.map {
            val changeData = ChangeData(type = it.changeType,
                    oldFileName = it.oldPath,
                    newFileName = it.newPath,
                    commitID = revCommit.name,
                    annotatedLines = if (revCommit.parents.size > 1) blame(repository, it.newPath, revCommit.name) else ArrayList())
            setDiff(repository, it, changeData)
            changeData
        }
    }

    @Throws(IOException::class)
    private fun getDiffsBetweenCommits(repository: Repository, parentTreeIterator: AbstractTreeIterator, currentCommitTreeIterator: AbstractTreeIterator): List<DiffEntry> {
        val df = DiffFormatter(DisabledOutputStream.INSTANCE)
        df.setRepository(repository)
        df.setDiffComparator(RawTextComparator.DEFAULT)
        df.isDetectRenames = true
        return df.scan(parentTreeIterator, currentCommitTreeIterator)
    }


    private fun setDiff(repository: Repository, diff: DiffEntry, repoChangeBlock: ChangeData) {
        val out = ByteArrayOutputStream()
        val df = DiffFormatter(out)
        df.setRepository(repository)

        try {
            df.setContext(0)
            df.isDetectRenames = true
            df.format(diff)
            val modifications = out.toString()
            out.reset()
            repoChangeBlock.diff = modifications
        } catch (e: IOException) {
            log.error("DiffParser between commits could not be parsed correctly!", e)
        }

    }

    private fun trimList(lines: List<String>): List<String> {
        for (i in lines.indices) {
            if (lines[i].startsWith("@@")) {
                return lines.subList(i, lines.size)
            }
        }

        return emptyList()
    }

    companion object {

        val REPOS_PATH = APP_FOLDER_PATH + File.separator + "repos"
        private val log = LoggerFactory.getLogger(GitClient::class.java)
    }

}