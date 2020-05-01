package org.dxworks.inspectorgit.compassmetrics

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.jira.TaskImporter
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val defaultTasksPair = Paths.get("tasks.json") to Paths.get("out/task-metrics.json")
val defaultCodePair = Paths.get("repo") to Paths.get("out/code-metrics.json")
private const val pathsDelimiter = ">"

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
fun main(args: Array<String>) {
    val tasksPair = getPathsPair("tasks", args)
    val codePair = getPathsPair("repo", args)
    val period = getPeriod(args)

    if (tasksPair == null && codePair == null) {
        createAnalyzeAndExport(defaultCodePair, defaultTasksPair, period)
    } else if (args.contains("-andDefault")) {
        createAnalyzeAndExport(codePair ?: defaultCodePair, tasksPair ?: defaultTasksPair, period)
    } else {
        createAnalyzeAndExport(codePair, tasksPair, period)
    }

}


private fun getPathsPair(argName: String, args: Array<String>): Pair<Path, Path>? {
    val argIn = getArg("${argName}In", args)
    val argOut = getArg("${argName}Out", args)
    return argIn?.let { argOut?.let { Paths.get(argIn) to Paths.get(argOut) } } ?: run {
        println("Args for $argName are not correct. Received $argIn and $argOut")
        null
    }
}

private fun getArg(argName: String, args: Array<String>): String? {
    val prefix = "-$argName="
    return args.find { it.startsWith(prefix) }?.removePrefix(prefix)
}

private fun getPeriod(args: Array<String>): Period? {
    val datesString = getArg("period", args)
    return if (datesString != null) {
        val (startDate, endDate) = getDates(datesString)
        Period(startDate, endDate)
    } else null
}

fun getDates(datesString: String): Pair<ZonedDateTime, ZonedDateTime> {
    try {
        val datesList = datesString.split(":").map { LocalDate.parse(it, dateFormatter) }
        return datesList[0].atTime(LocalTime.MIN).atZone(ZoneId.of("Z")) to
                datesList[1].atTime(LocalTime.MAX).atZone(ZoneId.of("Z"))
    } catch (e: Exception) {
        error("Wrong period format: $datesString")
    }
}

private fun createAnalyzeAndExport(codePair: Pair<Path, Path>?, tasksPair: Pair<Path, Path>?, period: Period?) {
    codePair?.let {
        run {
            val project = getCodeProject(it.first)
            val results = analyzeCode(project, period)
            export(results, it.second)
        }
    }
    tasksPair?.let {
        run {
            val project = TaskImporter().import(it.first)
            val results = analyzeTasks(project, period)
            export(results, it.second)
        }
    }
}

fun export(results: Any, path: Path) {
    path.parent.toFile().mkdirs()
    jacksonObjectMapper().writeValue(path.toFile(), results)
}

private fun getCodeProject(repoPath: Path): Project {
    val gitClient = GitClient(repoPath)
    val gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())

    return ProjectTransformer(gitLogDTO).transform()
}

