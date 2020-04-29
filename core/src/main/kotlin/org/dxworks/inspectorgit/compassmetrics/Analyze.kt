package org.dxworks.inspectorgit.compassmetrics

import org.dxworks.inspectorgit.model.Project
import java.time.ZonedDateTime


fun analyzeCode(project: Project, period: Period?): Map<String, Double> {
    val allCommits = project.commitRegistry.all
    val changes = allCommits.filter { periodFilter(period, it.committerDate) }.flatMap { it.changes }
    val codeChurn = Pair("Code Churn", changes.flatMap { it.hunks }
            .filter {
                it.addedLines.isNotEmpty() && it.deletedLines
                        .any { lineChange -> periodFilter(period, lineChange.content.commit.committerDate) }
            }
            .flatMap { it.deletedLines }.count { periodFilter(period, it.content.commit.committerDate) }.toDouble()
    )
    return mapOf(codeChurn)
}

private fun periodFilter(period: Period?, date: ZonedDateTime) = period?.contains(date) ?: true

fun analyzeTasks(project: Project, period: Period?): Map<String, Double> {
//    project.taskRegistry.allDetailedTasks.filter { it.changes.find { it. } }
    return emptyMap()
}
