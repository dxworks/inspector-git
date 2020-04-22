package org.dxworks.inspectorgit.fppt

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import java.io.File
import java.nio.file.Paths

const val configFilePathString = "igconf"
val outputFolder: File = Paths.get(System.getProperty("user.home")).resolve("fpptigOutput").toFile()
fun main() {
    val configFile = Paths.get(configFilePathString).toFile()
    outputFolder.mkdirs()

    val configLines = if (configFile.exists()) configFile.readLines() else emptyList()

    val repoPath = Paths.get(System.getenv("FPPT_IG_REPO_PATH") ?: configLines[0])
    val taskPrefix = System.getenv("FPPT_IG_TASK_PREFIX") ?: configLines[1]
    val taskRegex = "(\\b|'|\")$taskPrefix-\\d+(\\b|\"|')".toRegex()

    val gitClient = GitClient(repoPath)
    val gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())

    val projectName = repoPath.fileName.toString()
    val project = ProjectTransformer(gitLogDTO, projectName).transform()

    val allAuthors = project.authorRegistry.all
    val allAuthorIds = allAuthors.map { it.id }
    val authorIdToCommitsMap = allAuthors.map { it.id to it.allCommits }.toMap()

    val allCommits = project.commitRegistry.all
    val allSmartCommits = allCommits.filter { taskRegex.containsMatchIn(it.message) }
    val taskIdToSmartCommitMap = mapOfCommitsByTaskId(allSmartCommits, taskRegex)

    val mapOfTaskDetails = taskIdToSmartCommitMap.map {
        val commits = it.value
        it.key to mapOf(
                "numberOfCommits" to commits.size,
                "numberOfFilesChanged" to commits.flatMap { it.changes.map { it.file } }.distinct().count()
        )
    }.toMap()
    val output = mapOf(
            "numberOfCommitters" to allAuthorIds.size,
            "numberOfCommits" to allCommits.size,
            "numberOfSmartCommits" to allSmartCommits.size,
            "committers" to allAuthorIds,
            "committerMetrics" to mapOf(
                    "numberOfCommits" to allAuthorIds.map { it.toString() to authorIdToCommitsMap[it]?.size }.toMap(),
                    "avgChangesPerCommit" to allAuthorIds.map {
                        val commits = authorIdToCommitsMap[it]
                        val avg = (commits?.map { it.changes.size }?.sum() ?: 0) / (commits?.size ?: 1)
                        it.toString() to avg
                    }.toMap()
            ),
            "commits" to allCommits.map {

                val changes = it.changes
                val hunks = changes.flatMap { it.hunks }
                val summary = mapOf(
                        "id" to it.id,
                        "message" to it.message,
                        "authors" to it.author.id.toString(),
                        "numberOfChanges" to changes.size,
                        "numberOfChangesPerType" to changes.groupingBy { it.type }.eachCount(),
                        "addedLines" to hunks.map { it.addedLines.size }.sum(),
                        "deletedLines" to hunks.map { it.deletedLines.size }.sum()
                )

                it.id to summary
            }.toMap(),
            "tasks" to mapOfTaskDetails.toSortedMap(compareByDescending { mapOfTaskDetails[it]!!["numberOfFilesChanged"] })
    )

    jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValue(outputFolder.resolve("$projectName.json"), output)
}

private fun mapOfCommitsByTaskId(allSmartCommits: List<Commit>, taskRegex: Regex): Map<String, List<Commit>> {

    val allTaskIds = allSmartCommits.flatMap { taskRegex.findAll(it.message).toList().map { it.value } }
    return allTaskIds.map { id -> id to allSmartCommits.filter { it.message.contains(id) } }.toMap()
}
