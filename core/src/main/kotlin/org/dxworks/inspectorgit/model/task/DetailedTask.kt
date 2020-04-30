package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.compassmetrics.Period
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import java.time.ZonedDateTime

class DetailedTask(id: String,
                   project: Project,
                   val self: String,
                   val summary: String,
                   val description: String?,
                   val type: TaskType?,
                   val typeName: String,
                   val status: TaskStatus,
                   val created: ZonedDateTime,
                   val updated: ZonedDateTime,
                   val creator: TaskAccount,
                   val reporter: TaskAccount?,
                   val assignee: TaskAccount?,
                   val priority: String,
                   val changes: List<TaskChange>,
                   val comments: List<TaskComment>,
                   val timeEstimate: Long,
                   val timeSpent: Long,
                   commits: List<Commit>,
                   var parent: Task? = null,
                   var subtasks: List<Task> = emptyList()
) : Task(id, project, commits) {
    fun getStatusCategoriesInPeriod(period: Period): List<TaskStatusCategory> {
        return getStatusesInPeriod(period).map { it.category }.distinct()
    }

    private val statusFieldName = "status"

    private fun getStatusesInPeriod(period: Period): List<TaskStatus> {
        val statusesInPeriod = changes
                .filter { it.changedField(statusFieldName) }
                .filter { period.contains(it.created) }
                .mapNotNull { it.getItemForField(statusFieldName) }
                .mapNotNull { it.to?.let { project.taskStatusRegistry.getById(it) } }

        val relevantStatuses: List<TaskStatus> = statusesInPeriod + getStatusAt(period.start)
        return relevantStatuses.distinct()
    }

    private fun getStatusAt(date: ZonedDateTime): TaskStatus {
        val statusChanges = changes
                .filter { it.changedField(statusFieldName) }
        return statusChanges
                .filter { it.created < date }
                .maxBy { it.created }
                ?.let { it.getItemForField(statusFieldName) }
                ?.let { it.to }
                ?.let { project.taskStatusRegistry.getById(it) }
                ?: statusChanges.minBy { it.created }
                        ?.let { it.getItemForField(statusFieldName) }
                        ?.let { it.from }
                        ?.let { project.taskStatusRegistry.getById(it) }
                ?: status
    }

    val allCommits: List<Commit>
        get() = commits + subtasks.flatMap { it.commits }
}