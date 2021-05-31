package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.dto.gitlog.simple.SimpleLogWriter
import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar git-extractor.jar <path to repo>")
        println("The output file will be in ./results named <repo name>.iglog")
        return
    }

    val repo = Paths.get(args[0])
    val simpleLog = args.size == 2 && args[1] == "-s"

    if (!repo.toFile().isDirectory)
        println("Path does not point to a directory")

    val extractionManager = MetadataExtractionManager(repo, Paths.get("./results"))
    extractionManager.extract()
    if (simpleLog) SimpleLogWriter().write(extractionManager.extractFile)
}
