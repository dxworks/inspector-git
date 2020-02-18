package org.dxworks.inspectorgit

import com.google.gson.Gson
import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.gitClient.parsers.LogParser
import org.dxworks.inspectorgit.model.Change
import org.dxworks.inspectorgit.model.File
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.utils.appFolderPath
import java.nio.file.Paths

const val csvHeader = "file, NC, NAC, NAB"

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Please provide repository path or a projectName.json file")
        return
    }
    val resultPath = Paths.get(if (args.size > 1) args[1] else ".")

    val path = Paths.get(args[0])
    val targetFile = path.toFile()
    val project = if (targetFile.isFile && targetFile.extension == "json") {
        val gitLogDTO = Gson().fromJson(targetFile.readText(), GitLogDTO::class.java)
        ProjectTransformer(gitLogDTO, targetFile.nameWithoutExtension).transform()
    } else {
        val gitClient = GitClient(path)
        val gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())
        val name = path.fileName.toString()
        if (args.contains("-cache")) {
            appFolderPath.toFile().mkdirs()
            appFolderPath.resolve("${name}.json").toFile().writeText(Gson().toJson(gitLogDTO))
        }
        ProjectTransformer(gitLogDTO, name).transform()
    }

    val resultFolder = resultPath.toFile()
    resultFolder.mkdirs()
    val file = Paths.get(resultFolder.path).resolve("fileAuthors.csv").toFile()
    file.createNewFile()

    val builder = StringBuilder()
    builder.append(csvHeader).append("\n")
    project.fileRegistry.all.filter { it.isAlive }.forEach {
        val numberOfAllAuthorsForFile = getNumberOfAllAuthorsForFile(it)
        val numberOfAuthorsOfCurrentCodeInFile = getNumberOfAuthorsOfCurrentCodeInFile(it.lastChange!!)

        builder.append("${it.id}, ${it.changes.size}, $numberOfAllAuthorsForFile, $numberOfAuthorsOfCurrentCodeInFile\n")
    }
    file.writeText(builder.toString())
}

fun getNumberOfAuthorsOfCurrentCodeInFile(change: Change) = change.annotatedLines.map { it.content.commit.author.id }.distinct().count()

fun getNumberOfAllAuthorsForFile(file: File) = file.changes.map { it.commit.author.id }.distinct().count()
