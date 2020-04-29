package org.dxworks.inspectorgit.compassmetrics

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.jira.TaskImporter
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val defaultTasksPair = Pair(Path.of("tasks.json"), Path.of("out/task-metrics.json"))
val defaultCodePair = Pair(Path.of("repo"), Path.of("out/code-metrics.json"))
private const val pathsDelimiter = ":"

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
    val argValue = getArg(argName, args)
    return try {
        argValue?.split(pathsDelimiter)?.let { Pair(Path.of(it[0]), Path.of(it[1])) }
    } catch (e: Exception) {
        error("Could not parse argument $argName. Format should be: -$argName=path/to/input:path/to/output")
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

private fun getDates(datesString: String): Pair<LocalDate, LocalDate> {
    try {
        val datesList = datesString.split(pathsDelimiter).map { LocalDate.parse(it, dateFormatter) }
        return Pair(datesList[0], datesList[1])
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

