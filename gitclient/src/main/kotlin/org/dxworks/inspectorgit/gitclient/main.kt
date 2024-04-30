package org.dxworks.inspectorgit.gitclient

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.incognito.processGitLogFileIncognito
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread
import kotlin.io.path.absolutePathString
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
const val recursiveFlag = "--recursive"
const val recursiveEnv = "IG_RECURSIVE"

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
    val recursive = argsList.contains(recursiveFlag) || (System.getenv(recursiveEnv)?.toBoolean() ?: false)

    argsList.remove(gitFlag)
    argsList.remove(igFlag)
    argsList.remove(incognitoFlag)
    argsList.remove(recursiveFlag)

    if (args.size != 1)
        println(usage)

    val repo = Paths.get(args[0])

    if (!repo.toFile().isDirectory)
        println("Path does not point to a directory")

    val dotGitDir = repo.resolve(".git")

    val resultsPath = Paths.get("results")

    if (!resultsPath.exists()) {
        resultsPath.toFile().mkdirs()
    }

    if (dotGitDir.exists() && dotGitDir.isDirectory() && !recursive) {
        println("Provided directory is a Git repository. Analysing...")
        extractRepo(ig, repo, incognito, git, resultsPath)
    } else {
        if (!recursive) {
            println("Provided directory is NOT a Git repository. Searching for children repositories...")
            repo.toFile().listFiles().orEmpty()
                .filter { it.resolve(".git").exists() && it.resolve(".git").isDirectory }
                .forEach {
                    println("Found git repository under ${it.normalize().absoluteFile}. Extracting...")
                    extractRepo(ig, it.toPath(), incognito, git, resultsPath)
                }
        } else {
            println("Recursively searching for children repositories...")

            val repoToPath = repo.toFile().walkTopDown()
                .filter { it.resolve(".git").exists() && it.resolve(".git").isDirectory }
                .map { it.toPath().toAbsolutePath().normalize() }
                .map { it to repo.relativize(it) }
                .map { (it, repo) -> repo.toString().replace("/", "--") to it }
                .toMap()

            repoToPath.forEach { (prefix, path) ->
                println("Found git repository under ${path.absolutePathString()}. Extracting...")
                extractRepo(ig, path.resolve(".git"), incognito, git, resultsPath, prefix)
            }

            jacksonObjectMapper().writeValue(resultsPath.resolve("index.json").toFile(), repoToPath.mapValues { repo.relativize(it.value).toString() })
        }
    }

    println("\n\nResults will be available at ${resultsPath.toFile().normalize().absolutePath}")
}

private fun extractRepo(
    ig: Boolean,
    repo: Path,
    incognito: Boolean,
    git: Boolean,
    resultsPath: Path,
    prefix: String? = null,
) {

    val threads: MutableList<Thread> = ArrayList()

    if (ig) {
        threads.add(thread {
            MetadataExtractionManager(
                repo,
                if (prefix == null) resultsPath.resolve("${repo.name}.iglog") else resultsPath.resolve("$prefix.iglog"),
                incognito
            ).extract()
        })
    }
    if (git) {
        threads.add(thread {
            GitClient(repo).getSimpleLog(resultsPath.resolve(if(prefix == null) "${repo.name}.git" else "$prefix.git").toFile())
                .also { if (incognito) processGitLogFileIncognito(it) }
        })
    }
    threads.forEach { it.join() }
}
