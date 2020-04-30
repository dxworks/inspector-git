package org.dxworks.inspectorgit.compassmetrics

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.task.TaskStatusCategory
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


fun analyzeTasks(project: Project, period: Period?): Map<String, Double> {
    if (project.taskRegistry.isEmpty())
        return emptyMap()

    val period = period ?: project.taskRegistry.period
    val allTasks = project.taskRegistry.allDetailedTasks
    val tasks = allTasks.filter {
        it.getStatusCategoriesInPeriod(period)
                .contains(project.taskStatusCategoryRegistry.getById(TaskStatusCategory.indeterminate))
    }

    return emptyMap()
}


private fun periodFilter(period: Period?, date: ZonedDateTime) = period?.contains(date) ?: true
