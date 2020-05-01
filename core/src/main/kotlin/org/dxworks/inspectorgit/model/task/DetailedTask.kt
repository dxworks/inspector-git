package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.compassmetrics.Period
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class DetailedTask(id: String,
                   project: Project,
                   val self: String,
                   val summary: String,
                   val description: String?,
                   val type: TaskType,
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
                   val timeEstimate: Long?,
                   val timeSpent: Long?,
                   commits: List<Commit>,
                   var parent: Task? = null,
                   var subtasks: List<Task> = emptyList()
) : Task(id, project, commits) {
    fun getStatusCategoriesInPeriod(period: Period): List<TaskStatusCategory> {
        return getStatusesInPeriod(period).map { it.category }.distinct()
    }

    private val statusFieldName = "status"

    private fun getStatusesInPeriod(period: Period): List<TaskStatus> {
        val statusesInPeriod = getStatusChanges()
                .filter { period.contains(it.created) }
                .mapNotNull { it.getItemForField(statusFieldName) }
                .mapNotNull { it.to?.let { project.taskStatusRegistry.getById(it) } }

        val relevantStatuses: List<TaskStatus> = statusesInPeriod + getStatusAt(period.start)
        return relevantStatuses.distinct()
    }

    private fun getStatusAt(date: ZonedDateTime): TaskStatus {
        val statusChanges = getStatusChanges()
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

    private fun getStatusChanges() = changes
            .filter { it.changedField(statusFieldName) }

    fun isReopened() = getStatusChanges()
            .mapNotNull { it.getItemForField(statusFieldName) }
            .filter { it.from?.let { project.taskStatusRegistry.isDone(it) } ?: false }
            .all { it.to?.let { project.taskStatusRegistry.isDone(it) } ?: false }.not()

    fun getTimeToClose(): Long? {
        val statusChanges = getStatusChanges()
        val closedDate = statusChanges
                .filter {
                    it.getItemForField(statusFieldName)?.to?.let { project.taskStatusRegistry.isDone(it) } ?: false
                }
                .minBy { it.created }?.created
                ?: return null
        val openedDate = statusChanges
                .filter {
                    it.getItemForField(statusFieldName)?.to?.let { project.taskStatusRegistry.isIndeterminate(it) } ?: false
                            && it.getItemForField(statusFieldName)?.from?.let { project.taskStatusRegistry.isNew(it) } ?: false
                }.maxBy { it.created }?.created
                ?: statusChanges.filter {
                    it.getItemForField(statusFieldName)?.to?.let { !project.taskStatusRegistry.isDone(it) } ?: true
                }.maxBy { it.created }?.created
                ?: created

        return ChronoUnit.SECONDS.between(openedDate, closedDate)
    }

    val allCommits: List<Commit>
        get() = commits + subtasks.flatMap { it.commits }
}