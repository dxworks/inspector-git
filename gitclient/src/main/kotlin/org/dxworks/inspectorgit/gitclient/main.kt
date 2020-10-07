package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar git-extractor.jar <path to repo>")
        println("The output file will be in the repo named <repo name>.iglog")
        return
    }

    val repo = Paths.get(args[0])

    if (!repo.toFile().isDirectory)
        println("Path does not point to a directory")

    MetadataExtractionManager(repo, repo).extract()
}
