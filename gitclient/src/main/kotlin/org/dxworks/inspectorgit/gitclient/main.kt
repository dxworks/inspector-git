package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar git-extractor.jar <config file>")
        println("The first line of the config file should contain the output folder.")
        println("The following lines should contain the repositories one on each line")
        return
    }

    val configFile = Paths.get(args[0]).toFile()

    val lines = configFile.readLines()
    val outputPath = Paths.get(lines[0])

    lines.drop(1).parallelStream().forEach { MetadataExtractionManager(Paths.get(it), outputPath).extract() }
}