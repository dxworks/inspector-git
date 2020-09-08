package org.dxworks.inspectorgit.model.issuetracker

import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.remote.PullRequest
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class DetailedIssue(id: String,
                    project: IssueTrackerProject,
                    val self: String,
                    val summary: String,
                    val description: String?,
                    val type: IssueType,
                    val typeName: String,
                    val status: IssueStatus,
                    val created: ZonedDateTime,
                    val updated: ZonedDateTime,
                    val creator: IssueTrackerAccount,
                    val reporter: IssueTrackerAccount?,
                    val assignee: IssueTrackerAccount?,
                    val priority: String,
                    val changes: List<IssueChange>,
                    val comments: List<IssueComment>,
                    val timeEstimate: Long?,
                    val timeSpent: Long?,
                    val customFields: Map<String, Any>,
                    commits: Set<Commit> = emptySet(),
                    var parent: Issue? = null,
                    var subtasks: List<Issue> = emptyList(),
                    var pullRequests: Set<PullRequest> = emptySet()
) : Issue(id, project, commits) {
    fun getStatusCategoriesInPeriod(period: Period): List<IssueStatusCategory> {
        return getStatusesInPeriod(period).map { it.category }.distinct()
    }

    private val statusFieldName = "status"

    private fun getStatusesInPeriod(period: Period): List<IssueStatus> {
        val statusesInPeriod = getStatusChanges()
                .filter { period.contains(it.created) }
                .mapNotNull { it.getItemForField(statusFieldName) }
                .mapNotNull { it.to?.let { project.issueStatusRegistry.getById(it) } }

        val relevantStatuses: List<IssueStatus> = statusesInPeriod + getStatusAt(period.start)
        return relevantStatuses.distinct()
    }

    private fun getStatusAt(date: ZonedDateTime): IssueStatus {
        val statusChanges = getStatusChanges()
        return statusChanges
                .filter { it.created < date }
                .maxBy { it.created }
                ?.let { it.getItemForField(statusFieldName) }
                ?.let { it.to }
                ?.let { project.issueStatusRegistry.getById(it) }
                ?: statusChanges.minBy { it.created }
                        ?.let { it.getItemForField(statusFieldName) }
                        ?.let { it.from }
                        ?.let { project.issueStatusRegistry.getById(it) }
                ?: status
    }

    private fun getStatusChanges() = changes
            .filter { it.changedField(statusFieldName) }

    fun isReopened() = getStatusChanges()
            .mapNotNull { it.getItemForField(statusFieldName) }
            .filter { it.from?.let { project.issueStatusRegistry.isDone(it) } ?: false }
            .all { it.to?.let { project.issueStatusRegistry.isDone(it) } ?: false }.not()

    fun getTimeToClose(): Double? {
        val statusChanges = getStatusChanges()
        val closedDate = statusChanges
                .filter {
                    it.getItemForField(statusFieldName)?.to?.let { project.issueStatusRegistry.isDone(it) } ?: false
                }
                .minBy { it.created }?.created
                ?: return null
        val openedDate = statusChanges
                .filter {
                    it.getItemForField(statusFieldName)?.to?.let { project.issueStatusRegistry.isIndeterminate(it) } ?: false
                            && it.getItemForField(statusFieldName)?.from?.let { project.issueStatusRegistry.isNew(it) } ?: false
                }.maxBy { it.created }?.created
                ?: statusChanges.filter {
                    it.getItemForField(statusFieldName)?.to?.let { !project.issueStatusRegistry.isDone(it) } ?: true
                }.maxBy { it.created }?.created
                ?: created


        return asABitLessThen8hPerDay(ChronoUnit.SECONDS.between(openedDate, closedDate))
    }

    private fun asABitLessThen8hPerDay(between: Long) = between / 3.2
}