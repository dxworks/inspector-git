package org.dxworks.inspectorgit.compassmetrics

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.task.DetailedTask
import org.dxworks.inspectorgit.model.task.TaskStatusCategory
import java.nio.file.Paths
import java.util.*

private const val bug = "bug"

private const val prioritiesFilePath = "config/jira-priorities.properties"

fun analyzeTasks(project: Project, period: Period?): Map<String, Double> {
    if (project.taskRegistry.isEmpty())
        return emptyMap()

    val period = period ?: project.taskRegistry.period
    val allTasks = project.taskRegistry.allDetailedTasks
    val tasks = allTasks.filter {
        it.getStatusCategoriesInPeriod(period)
                .contains(project.taskStatusCategoryRegistry.getById(TaskStatusCategory.indeterminate))
    }

    val (reopened, other) = tasks.partition { it.isReopened() }

    val (bugFixes, development) = other
            .filter { it.getStatusCategoriesInPeriod(period).contains(project.taskStatusCategoryRegistry.getById(TaskStatusCategory.done)) }
            .partition { isBugTask(it) }
    val bugFixingTime = getSpentTime(bugFixes).toDouble()
    val developmentTime = getSpentTime(development).toDouble()

    val defectDensity = "Defect Density" to (bugFixingTime / (bugFixingTime + developmentTime))

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

fun getSpentTime(tasks: List<DetailedTask>) =
        tasks.mapNotNull { it.timeSpent ?: it.timeEstimate ?: it.getTimeToClose() }.sum() / 60


private fun isBugTask(it: DetailedTask) =
        it.type.name.contains(bug, true) || it.typeName.contains(bug, true)
