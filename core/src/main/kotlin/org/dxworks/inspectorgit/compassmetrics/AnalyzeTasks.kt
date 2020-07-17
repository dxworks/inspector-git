package org.dxworks.inspectorgit.compassmetrics

import org.dxworks.inspectorgit.model.ComposedProject
import org.dxworks.inspectorgit.model.issuetracker.DetailedIssue
import org.dxworks.inspectorgit.model.issuetracker.IssueStatusCategory
import java.nio.file.Paths
import java.util.*

private const val bug = "bug"

private const val prioritiesFilePath = "config/jira-priorities.properties"

fun analyzeTasks(composedProject: ComposedProject, period: Period?): Map<String, Double> {
    if (composedProject.issueRegistry.isEmpty())
        return emptyMap()

    val actualPeriod = period ?: composedProject.issueRegistry.period
    val allTasks = composedProject.issueRegistry.allDetailedIssues
    val tasksActiveInPeriod = allTasks.filter {
        it.getStatusCategoriesInPeriod(actualPeriod)
                .contains(composedProject.issueStatusCategoryRegistry.getById(IssueStatusCategory.indeterminate))
    }

    val tasks = tasksActiveInPeriod.filterNot { it.isReopened() }

    val (bugFixes, development) = tasks
            .filter { it.getStatusCategoriesInPeriod(actualPeriod).contains(composedProject.issueStatusCategoryRegistry.getById(IssueStatusCategory.done)) }
            .partition { isBugTask(it) }
    val bugFixingTime = getSpentTime(bugFixes)
    val developmentTime = getSpentTime(development)

    val defectDensity = "Defect Density" to (bugFixingTime / (bugFixingTime + developmentTime)) * 100

    val meanTimeToRepair = "Mean Time To Repair" to bugFixingTime / bugFixes.size

    val priorityProperties = readPriorityProperties()

    val defectsSeverity = "Defects Severity" to bugFixes.map { getValueForPriority(priorityProperties, it.priority) }.average()

    val defectFrequency = "Defects Frequency" to bugFixes.size.toDouble()

    return mapOf(defectDensity, meanTimeToRepair, defectsSeverity, defectFrequency)
}

private fun readPriorityProperties(): Map<String, Long> {
    val properties = Properties()
    properties.load(Paths.get(prioritiesFilePath).toFile().reader())
    val priorityProperties = properties.map { it.key.toString().toLowerCase() to it.value.toString().toLong() }.toMap();
    return priorityProperties
}

fun getValueForPriority(properties: Map<String, Long>, priority: String): Long {
    return properties[priority.toLowerCase()]
            ?: run {
                println("Could not find value for $priority. Using default instead")
                properties["default"]
            }
            ?: error("No value provided for default")
}

fun getSpentTime(issues: List<DetailedIssue>) =
        issues.mapNotNull { it.timeSpent?.toDouble() ?: it.timeEstimate?.toDouble() ?: it.getTimeToClose() }.sum() / 60


private fun isBugTask(it: DetailedIssue) =
        it.type.name.contains(bug, true) || it.typeName.contains(bug, true)
