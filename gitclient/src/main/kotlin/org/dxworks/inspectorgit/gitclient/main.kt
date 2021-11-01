package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import java.nio.file.Paths
import java.util.*

val version by lazy {
    Properties().apply { load(object {}::class.java.classLoader.getResourceAsStream("maven.properties")) }["version"]
}

val versionCommandArgs = setOf("-v", "version", "--version", "-version", "-V")

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar iglog.jar <path to repo>")
        println("The output file will be in ./results named <repo name>.iglog")
        return
    }

    if(versionCommandArgs.contains(args[0])) {
        println("Iglog $version")
        return
    }

    val repo = Paths.get(args[0])

    if (!repo.toFile().isDirectory)
        println("Path does not point to a directory")

    MetadataExtractionManager(repo, Paths.get("./results")).extract()
}
