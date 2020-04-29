package org.dxworks.inspectorgit.fppt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.dxworks.inspectorgit.AccountMergeTool
import org.dxworks.inspectorgit.fppt.configuration.FpptConfiguration
import org.dxworks.inspectorgit.fppt.configuration.FpptConfigurer
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.jira.TaskImporter
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.GitAccount
import org.dxworks.inspectorgit.model.task.DetailedTask
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import java.io.File
import java.nio.file.Paths

private val outputFolder: File = Paths.get(System.getProperty("user.home")).resolve("fpptigOutput").toFile()

fun main() {
    outputFolder.mkdirs()

    val configuration = FpptConfigurer().getConfiguration()
    val projectName = configuration.repositoryPath.toAbsolutePath().normalize().fileName.toString()

    val project = createProject(configuration, projectName)

    val allAuthors = project.accountRegistry.getAll<GitAccount>()
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

private fun createProject(configuration: FpptConfiguration, projectName: String): Project {
    val gitClient = GitClient(configuration.repositoryPath)
    val gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())

    val project = ProjectTransformer(gitLogDTO, projectName).transform()

    configuration.tasksFilePath?.let { TaskImporter().import(it, configuration.taskPrefixes, project) }
    configuration.devMergesFilePath?.let { AccountMergeTool(project).mergeAll(jacksonObjectMapper().readValue(it.toFile())) }
    return project
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