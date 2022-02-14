package org.dxworks.inspectorgit.gitclient.jgit

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.utils.systemsFolderPath
import org.eclipse.jgit.api.CloneCommand
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.RefNotFoundException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectLoader
import org.eclipse.jgit.lib.ObjectReader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
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
import java.util.stream.Collectors
import kotlin.system.measureTimeMillis


class JGitClient {

    @Throws(IOException::class)
    private fun getRepository(repoPath: String): Git {
        return Git.open(Paths.get(repoPath).toFile())
    }

    @Throws(GitAPIException::class)
    fun checkoutRevision(revisionName: String, repoPath: String) {
        try {
            val git: Git = getRepository(repoPath)
            git.checkout().setName(revisionName).call()
        } catch (ex: IOException) {
            LOG.error("could not find repository for $repoPath", ex)
        } catch (ex: RefNotFoundException) {
            checkoutRemoteRevision(revisionName, repoPath)
        } catch (e: GitAPIException) {
            throw e
        }
    }

    @Throws(GitAPIException::class)
    private fun checkoutRemoteRevision(revisionName: String, repoPath: String) {
        try {
            val git: Git = getRepository(repoPath)
            git.checkout()
                .setCreateBranch(true)
                .setName(revisionName)
                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK)
                .setStartPoint("origin/$revisionName")
                .call()
        } catch (ex: IOException) {
            LOG.error("could not find repository for $repoPath", ex)
        }
    }

    @Throws(GitAPIException::class)
    fun cloneAndInitializeRepository(
        repositoryURL: String,
        projectID: String?,
        cloneAllBranches: Boolean?,
        username: String?,
        password: String?,
        mainBranch: String?
    ) {
        val repoName = repositoryURL.replace(".git", "").substring(repositoryURL.lastIndexOf("/") + 1)
        val cloneCommand: CloneCommand = Git.cloneRepository()
            .setURI(repositoryURL)
            .setDirectory(systemsFolderPath.resolve(Paths.get(projectID, "repository", repoName)).toFile())
            .setBranchesToClone(setOf(mainBranch))
            .setBranch(mainBranch)
        if (username != null && password != null) cloneCommand.setCredentialsProvider(
            UsernamePasswordCredentialsProvider(username, password)
        )
        cloneCommand.call()
    }

    @JvmOverloads
    @Throws(GitAPIException::class)
    fun updateProject(repoPath: String, username: String? = null, password: String? = null) {
        val git: Git
        git = try {
            getRepository(repoPath)
        } catch (e: IOException) {
            LOG.error("could not find repository $repoPath", e)
            return
        }
        val pullCommand: PullCommand = git.pull()
        if (username != null && password != null) pullCommand.setCredentialsProvider(
            UsernamePasswordCredentialsProvider(
                username,
                password
            )
        )
        pullCommand.call()
        //TODO: implement logs update!!!
        git.close()
    }

    @Throws(GitAPIException::class)
    fun updateProject(repoName: String, branchName: String, username: String?, password: String?) {
        checkoutRevision(branchName, repoName)
        updateProject(repoName, username, password)
    }

    @Throws(IOException::class)
    fun getFileContentForProjectAndRevision(filePath: String?, repoName: String, revisionName: String?): String {
        //not tested
        val repository: Repository = getRepository(repoName).repository
        val lastCommitId: ObjectId = repository.resolve(revisionName)
        try {
            RevWalk(repository).use { revWalk ->
                TreeWalk(repository).use { treeWalk ->
                    val commit: RevCommit = revWalk.parseCommit(lastCommitId)
                    val tree: RevTree = commit.tree
                    treeWalk.addTree(tree)
                    treeWalk.isRecursive = true
                    treeWalk.filter = PathFilter.create(filePath)
                    if (!treeWalk.next()) {
                        println("File not found")
                        throw FileNotFoundException(filePath)
                    }
                    val objectId: ObjectId = treeWalk.getObjectId(0)
                    val loader: ObjectLoader = repository.open(objectId)
                    return String(loader.bytes)
                }
            }
        } catch (ex: FileNotFoundException) {
            throw ex
        }
    }

    fun generateGitLogForDx(repoNames: List<String>): Map<String, List<CommitDTO>> {
        return repoNames.stream().collect(Collectors.toMap<String, String, List<CommitDTO>>(
            { repoName: String? ->
                Paths.get(
                    repoName
                ).fileName.toString()
            }
        ) { repoPath: String ->
            this.generateGitLogForDx(
                repoPath
            )
        })
    }

    fun generateGitLogForDx(repoPath: String): List<CommitDTO> {
        try {
            val git: Git = getRepository(repoPath)
            return git.log().call()
                .map { revCommit ->
                    getCommitDetails(
                        git.repository,
                        revCommit
                    )
                }
        } catch (e: GitAPIException) {
            LOG.error("Git Api error", e)
        } catch (e: IOException) {
            LOG.error("could not find repository $repoPath", e)
            // TODO: maybe remove the repo from project
        }
        return emptyList()
    }

    private fun getCommitDetails(repository: Repository, revCommit: RevCommit): CommitDTO {
        return CommitDTO(
            id = revCommit.name,
            parentIds = revCommit.parents.map { it.name },
            authorName = revCommit.authorIdent.name,
            authorEmail = revCommit.authorIdent.emailAddress,
            authorDate = (revCommit.commitTime * 1000L).toString(),
            committerName = revCommit.committerIdent.name,
            committerEmail = revCommit.committerIdent.emailAddress,
            committerDate = (revCommit.commitTime * 1000L).toString(),
            message = revCommit.fullMessage,
            changes = getCommitChanges(repository, revCommit)
        )
    }

    private fun getCommitChanges(repository: Repository, revCommit: RevCommit): List<ChangeDTO> {
        val reader: ObjectReader = repository.newObjectReader()
        var parentTreeIterator: AbstractTreeIterator = CanonicalTreeParser()
        val currentCommitTreeIterator = CanonicalTreeParser()
        try {
            return DiffFormatter(DisabledOutputStream.INSTANCE).use { df ->
                if (revCommit.parentCount == 0) {
                    parentTreeIterator = EmptyTreeIterator()
                    getChanges(
                        currentCommitTreeIterator,
                        reader,
                        revCommit,
                        df,
                        repository,
                        parentTreeIterator,
                        null
                    )
                } else {
                    revCommit.parents.mapNotNull { parentCommit ->
                        (parentTreeIterator as CanonicalTreeParser).reset(reader, parentCommit.tree.id)
                        getChanges(
                            currentCommitTreeIterator,
                            reader,
                            revCommit,
                            df,
                            repository,
                            parentTreeIterator,
                            parentCommit
                        )
                    }.flatten()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }

    private fun getChanges(
        currentCommitTreeIterator: CanonicalTreeParser,
        reader: ObjectReader,
        revCommit: RevCommit,
        df: DiffFormatter,
        repository: Repository,
        parentTreeIterator: AbstractTreeIterator,
        parentCommit: RevCommit?
    ): List<ChangeDTO> {
        currentCommitTreeIterator.reset(reader, revCommit.tree.id)
        df.setRepository(repository)
        df.setDiffComparator(RawTextComparator.DEFAULT)
        df.isDetectRenames = true
        return df.scan(parentTreeIterator, currentCommitTreeIterator)
            ?.map {
                jGitChangeFromDiff(repository, it, parentCommit)
            } ?: emptyList()
    }

    private fun jGitChangeFromDiff(
        repository: Repository,
        it: DiffEntry,
        parentCommit: RevCommit?
    ): JGitChangeDTO {
        val modifications = getModifications(repository, it)
        var numberOfAddedLines = 0
        var numberOfDeletedLines = 0
        var numberOfHunks = 0
        val isBinary = modifications.any { it.startsWith("Binary files") }
        if (!isBinary) {
            val (addedLines, rest) = modifications.partition { it.startsWith("+") }
            numberOfAddedLines = addedLines.size
            val (deletedLines, rest2) = rest.partition { it.startsWith("-") }
            numberOfDeletedLines = deletedLines.size
            numberOfHunks = rest2.count { it.startsWith("@") }
        }
        return JGitChangeDTO(
            type = mapChangeType(it.changeType),
            oldFileName = it.oldPath,
            newFileName = it.newPath,
            parentCommitId = parentCommit?.name ?: "",
            isBinary = false,
            addedLines = numberOfAddedLines,
            deletedLines = numberOfDeletedLines,
            numberOfHunks = numberOfHunks
        )
    }

    private fun getModifications(repository: Repository?, diff: DiffEntry?): List<String> = try {
        ByteArrayOutputStream().use { out ->
            return DiffFormatter(out).use { df ->
                //Set the repository the formatter can load object contents from.
                //A DiffEntry is 'A value class representing a change to a file' therefore for each file you have a diff entry
                df.setRepository(repository)
                df.setContext(0)
                df.isDetectRenames = true
                df.format(diff)
                val r = RawText(out.toByteArray())
                r.lineDelimiter
                val modifications = out.toString()
                out.reset()
                modifications.split("\n")
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        emptyList()
    }

    private fun trimList(lines: List<String>): List<String> {
        for (i in lines.indices) {
            if (lines[i].startsWith("@@")) return lines.subList(i, lines.size)
        }
        return emptyList()
    }

    private fun mapChangeType(changeType: DiffEntry.ChangeType): ChangeType {
        return when (changeType) {
            DiffEntry.ChangeType.ADD -> ChangeType.ADD
            DiffEntry.ChangeType.MODIFY -> ChangeType.MODIFY
            DiffEntry.ChangeType.DELETE -> ChangeType.DELETE
            DiffEntry.ChangeType.RENAME -> ChangeType.RENAME
            else -> ChangeType.ADD
        }
    }

    @Throws(IOException::class)
    fun checkLocalRepo(repoPath: String): Git {
        return Git.open(Paths.get(repoPath, if (repoPath.endsWith(".git")) "" else ".git").toFile())
    }

    @Throws(IOException::class, GitAPIException::class)
    fun getCurrentBranch(repositoryPath: String): String {
        return getRepository(repositoryPath).repository.branch
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(GitClient::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            measureTimeMillis {
                jacksonObjectMapper().writeValue(
                    File("kafkaResult.json"),
                    JGitClient().generateGitLogForDx("/Users/darius/Documents/development/test-projects/kafka")
                )
            }.also { println("jgit took: ${it / 1000} s") }

            measureTimeMillis {
                GitClient(Paths.get("/Users/darius/Documents/development/test-projects/kafka")).getSimpleLog()
            }.also { println("simp took: ${it / 1000} s") }


//        try {
//            Repository repository = Git.cloneRepository()
//                    .setURI("https://Mario_Rivis@bitbucket.org/Mario_Rivis/dx-platform.git")
//                    .setDirectory(new File(new FileSystemServiceImpl().getFolderPathByProjectID("projectID", "repository")))
//                    .setCloneAllBranches(true)
//                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
//                            "mario.rivis@gmail.com", "_Just6and9"))
//                    .call()
//                    .getRepository();

//            final List<CommitModel> commits = new GitClient().generateGitLogForDx("projectID");

//            System.out.println(commits);

//                    Git git = Git.open(new File("E:\\dx-platform\\repos\\second-try\\.git"));
//                    Iterable<RevCommit> revCommits = git.log().call();
//
//                    revCommits.forEach(revCommit -> {
//                        System.out.println(revCommit.getName());
//                        System.out.println(revCommit.getFullMessage());
//                    });
//
//                    git.checkout().setName("87e0470561ccbb019ad24c6b154df4ccb55b21e5").call();

//                } catch (IOException e) {
//                    e.printStackTrace();
//        } catch (NoHeadException e) {
//            e.printStackTrace();
//        } catch (GitAPIException e) {
//            e.printStackTrace();
//        }
        }
    }
}
