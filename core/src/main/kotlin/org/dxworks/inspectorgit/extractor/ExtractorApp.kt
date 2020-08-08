package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.gitclient.extractors.MetadataExtractionManager
import org.dxworks.inspectorgit.gitclient.iglog.readers.IGLogReader
import org.dxworks.inspectorgit.model.git.Change
import org.dxworks.inspectorgit.model.git.File
import org.dxworks.inspectorgit.transformers.git.GitProjectTransformer
import org.dxworks.inspectorgit.utils.appFolderPath
import java.nio.file.Paths

const val csvHeader = "file, NC, NAC, NAB"

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: java -jar git-extractor.jar <repository path or cached iglog file path> <[optional] output directory>")
        println("The program wil output a file called fileAuthors.csv in the output directory(defaults to .).")
        println("Also the program caches its project data as iglog at ~/.inspectorgit")
        return
    }
    val resultPath = Paths.get(if (args.size > 1) args[1] else ".")

    val path = Paths.get(args[0]).toAbsolutePath().normalize()
    val targetFile = path.toFile()
    val project = if (targetFile.isFile && targetFile.extension == "iglog") {
        val gitLogDTO = IGLogReader().read(targetFile.inputStream())
        GitProjectTransformer(gitLogDTO, targetFile.nameWithoutExtension).transform()
    } else {
        MetadataExtractionManager(path, appFolderPath).extract()

        val gitLogDTO = IGLogReader().read(appFolderPath.resolve("${path.fileName}.iglog").toFile().inputStream())
        val name = path.fileName.toString()
        GitProjectTransformer(gitLogDTO, name).transform()
    }

    val resultFolder = resultPath.toFile()
    resultFolder.mkdirs()
    val file = Paths.get(resultFolder.path).resolve("fileAuthors.csv").toFile()
    file.createNewFile()

    val builder = StringBuilder()
    builder.append(csvHeader).append("\n")
    project.fileRegistry.all.filter { it.isAlive(null) }.forEach {
        val numberOfAllAuthorsForFile = getNumberOfAllAuthorsForFile(it)
        val numberOfAuthorsOfCurrentCodeInFile = getNumberOfAuthorsOfCurrentCodeInFile(it.getLastChange(null)!!)

        builder.append("${it.id}, ${it.changes.size}, $numberOfAllAuthorsForFile, $numberOfAuthorsOfCurrentCodeInFile\n")
    }
    println()
    println("Your file is ready at ${file.toPath().toAbsolutePath().normalize()}")
    file.writeText(builder.toString())
}

fun getNumberOfAuthorsOfCurrentCodeInFile(change: Change) = change.annotatedLines.map { it.content.commit.author.id }.distinct().count()

fun getNumberOfAllAuthorsForFile(file: File) = file.changes.map { it.commit.author.id }.distinct().count()
