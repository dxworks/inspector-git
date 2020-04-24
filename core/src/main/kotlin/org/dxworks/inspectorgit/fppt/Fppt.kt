package org.dxworks.inspectorgit.fppt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.fppt.dtos.task.TaskDTO
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.model.task.DetailedTask
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.dxworks.inspectorgit.transformers.TasksTransformer
import java.io.File
import java.nio.file.Paths

const val configFilePathString = "igconf"
val outputFolder: File = Paths.get(System.getProperty("user.home")).resolve("fpptigOutput").toFile()
fun main() {
    val configFile = Paths.get(configFilePathString).toFile()
    outputFolder.mkdirs()

    val configLines = if (configFile.exists()) configFile.readLines() else emptyList()
    val repoPath = Paths.get(System.getenv("FPPT_IG_REPO_PATH") ?: configLines[0])
    val taskPrefixes = (System.getenv("FPPT_IG_TASK_PREFIX") ?: configLines[1]).split(",").map { it.trim() }
    val tasksFilePath = Paths.get(System.getenv("FPPT_IG_TASKS_PATH") ?: configLines[2])

    val tasks = jacksonObjectMapper().readValue<List<TaskDTO>>(tasksFilePath.toFile())


    val gitClient = GitClient(repoPath)
    val gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())

    val projectName = repoPath.fileName.toString()
    val project = ProjectTransformer(gitLogDTO, projectName).transform()
    TasksTransformer(project, tasks, taskPrefixes).addToProject()

    val allAuthors = project.developerRegistry.all
    val accountIdToCommitsMap = allAuthors.map { it.id to it.commits }.toMap()

    val allCommits = project.commitRegistry.all
    val allAuthorIds = allCommits.map { it.author.id }.distinct()


    val mapOfTaskDetails = project.taskRegistry.all.map { task ->
        val numberOfCommits = task.commits.size
        val numberOfFiles = task.commits.flatMap { commit -> commit.changes.map { it.file } }.distinct().count()
        if (task is DetailedTask)
            task.id to TaskDetails(
                    summary = task.summary,
                    numberOfCommits = numberOfCommits,
                    numberOfFilesChanged = numberOfFiles
            )
        else
            task.id to BasicTaskDetails(
                    numberOfCommits = numberOfCommits,
                    numberOfFilesChanged = numberOfFiles
            )
    }.toMap()
    val output = mapOf(
            "numberOfCommitters" to allAuthorIds.size,
            "numberOfCommits" to allCommits.size,
            "numberOfSmartCommits" to project.taskRegistry.all.flatMap { it.commits }.distinct().count(),
            "committers" to allAuthorIds,
            "committerMetrics" to mapOf(
                    "numberOfCommits" to allAuthorIds.map { it to accountIdToCommitsMap[it]?.size }.toMap(),
                    "avgChangesPerCommit" to allAuthorIds.map { id ->
                        val commits = accountIdToCommitsMap[id]
                        val avg = commits?.map { it.changes.size }?.average() ?: 0
                        id to avg
                    }.toMap()
            ),
            "commits" to allCommits.map {

                val changes = it.changes
                val hunks = changes.flatMap { it.hunks }
                val summary = mapOf(
                        "id" to it.id,
                        "message" to it.message,
                        "author" to it.author.id,
                        "date" to it.authorDate.toString(),
                        "numberOfChanges" to changes.size,
                        "numberOfChangesPerType" to changes.groupingBy { it.type }.eachCount(),
                        "addedLines" to hunks.map { it.addedLines.size }.sum(),
                        "deletedLines" to hunks.map { it.deletedLines.size }.sum()
                )

                it.id to summary
            }.toMap().toSortedMap(compareBy { project.commitRegistry.getById(it)!!.authorDate }),
            "tasks" to mapOfTaskDetails.toSortedMap(compareByDescending { mapOfTaskDetails[it]!!.numberOfFilesChanged })
    )

    jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputFolder.resolve("$projectName.json"), output)
}

class TaskDetails(
        val summary: String,
        numberOfCommits: Int,
        numberOfFilesChanged: Int
) : BasicTaskDetails(
        numberOfCommits,
        numberOfFilesChanged
)

open class BasicTaskDetails(
        val numberOfCommits: Int,
        val numberOfFilesChanged: Int
)