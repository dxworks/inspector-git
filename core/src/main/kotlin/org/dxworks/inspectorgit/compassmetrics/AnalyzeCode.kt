package org.dxworks.inspectorgit.compassmetrics

import org.dxworks.inspectorgit.model.Project
import java.time.ZonedDateTime

fun analyzeCode(project: Project, period: Period?): Map<String, Double> {
    val allCommits = project.commitRegistry.all
    val changes = allCommits.filter { periodFilter(period, it.committerDate) }.flatMap { it.changes }
    val codeChurn = "Code Churn" to changes.flatMap { it.hunks }
            .filter {
                it.addedLines.isNotEmpty() && it.deletedLines
                        .any { lineChange -> periodFilter(period, lineChange.content.commit.committerDate) }
            }
            .flatMap { it.deletedLines }.count { periodFilter(period, it.content.commit.committerDate) }.toDouble()

    val `number of files with changes from at least two developers in this period` =
            changes.groupBy { it.file }
                    .filter { entry -> entry.value.distinctBy { it.commit.committer }.size > 1 }
                    .count().toDouble()
    val mergeBottlenecks = "Merge Bottlenecks" to `number of files with changes from at least two developers in this period`


    return mapOf(codeChurn, mergeBottlenecks)
}


private fun periodFilter(period: Period?, date: ZonedDateTime) = period?.contains(date) ?: true