package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.incognito.processLogfile
import java.nio.file.Paths
import java.util.*
import kotlin.concurrent.thread

val version by lazy {
    Properties().apply { load(object {}::class.java.classLoader.getResourceAsStream("maven.properties")) }["version"]
}

val versionCommandArgs = setOf("-v", "version", "--version", "-version", "-V")
const val igFlag = "-ig"
const val igEnv = "IG_IGLOG"
const val gitFlag = "-git"
const val gitEnv = "-IG_GITLOG"
const val incognitoFlag = "-incognito"
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

    val git = argsList.contains(gitFlag) || (System.getenv(gitEnv)?.toBoolean() ?: false)
    val ig = argsList.contains(igFlag) || (System.getenv(igEnv)?.toBoolean() ?: false)
    val incognito = argsList.contains(incognitoFlag) || (System.getenv(incognitoEnv)?.toBoolean() ?: false)

    argsList.remove(gitFlag)
    argsList.remove(igFlag)
    argsList.remove(incognitoFlag)

    if (args.size != 1)
        println(usage)

    val repo = Paths.get(args[0])

    if (!repo.toFile().isDirectory)
        println("Path does not point to a directory")

    val extractToPath = Paths.get("./results")

    val threads: MutableList<Thread> = ArrayList()

    if (ig) {
        threads.add(thread { MetadataExtractionManager(repo, extractToPath, incognito).extract() })
    }
    if (git) {
        threads.add(thread {
            GitClient(repo).getSimpleLog(extractToPath.resolve(repo.fileName.toString() + ".git").toString())
                .also { processLogfile(it) }
        })
    }
    threads.forEach { it.join() }
}
