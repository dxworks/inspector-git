package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.incognito.processGitLogFileIncognito
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name

val version by lazy {
    Properties().apply { load(object {}::class.java.classLoader.getResourceAsStream("maven.properties")) }["version"]
}

val versionCommandArgs = setOf("-v", "version", "--version", "-version", "-V")
const val igFlag = "--no-iglog"
const val igEnv = "IG_IGLOG"
const val gitFlag = "---no-gitlog"
const val gitEnv = "IG_GITLOG"
const val incognitoFlag = "--incognito"
const val incognitoEnv = "IG_INCOGNITO"

const val usage = """
    
"""

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar iglog.jar <path to repo>")
        println("The output file will be in ./results named <repo name>.iglog")
        return
    }

    if (versionCommandArgs.contains(args[0])) {
        println("Iglog $version")
        return
    }

    val argsList = args.toMutableList()

    val git = if (argsList.contains(gitFlag)) false else (System.getenv(gitEnv)?.toBoolean() ?: true)
    val ig = if (argsList.contains(igFlag)) false else (System.getenv(igEnv)?.toBoolean() ?: true)
    val incognito = argsList.contains(incognitoFlag) || (System.getenv(incognitoEnv)?.toBoolean() ?: false)

    argsList.remove(gitFlag)
    argsList.remove(igFlag)
    argsList.remove(incognitoFlag)

    if (args.size != 1)
        println(usage)

    val repo = Paths.get(args[0])

    if (!repo.toFile().isDirectory)
        println("Path does not point to a directory")

    val dotGitDir = repo.resolve(".git")

    val resultsPath = Paths.get("results")

    if(!resultsPath.exists()) {
        resultsPath.toFile().mkdirs()
    }

    if (dotGitDir.exists() && dotGitDir.isDirectory()) {
        println("Provided directory is a Git repository. Analysing...")
        extractRepo(ig, repo, incognito, git, resultsPath)
    } else {
        println("Provided directory is NOT a Git repository. Searching for children repositories...")
        repo.toFile().listFiles().orEmpty()
            .filter { it.resolve(".git").exists() && it.resolve(".git").isDirectory }
            .forEach {
                println("Found git repository under ${it.normalize().absoluteFile}. Extracting...")
                extractRepo(ig, it.toPath(), incognito, git, resultsPath)
            }
    }

    println("\n\nResults will be available at ${resultsPath.toFile().normalize().absolutePath}")
}

private fun extractRepo(
    ig: Boolean,
    repo: Path,
    incognito: Boolean,
    git: Boolean,
    resultsPath: Path
) {

    val threads: MutableList<Thread> = ArrayList()

    if (ig) {
        threads.add(thread { MetadataExtractionManager(repo, resultsPath, incognito).extract() })
    }
    if (git) {
        threads.add(thread {
            GitClient(repo).getSimpleLog(resultsPath.resolve(repo.name + ".git").toFile())
                .also { if (incognito) processGitLogFileIncognito(it) }
        })
    }
    threads.forEach { it.join() }
}
