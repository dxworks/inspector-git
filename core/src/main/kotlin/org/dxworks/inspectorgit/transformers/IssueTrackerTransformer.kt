package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.jira.dtos.IssueAccountDTO
import org.dxworks.inspectorgit.jira.dtos.IssueDTO
import org.dxworks.inspectorgit.jira.dtos.IssueStatusDTO
import org.dxworks.inspectorgit.jira.dtos.IssueTypeDTO
import org.dxworks.inspectorgit.model.issuetracker.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class IssueTrackerTransformer(
        private val issueStatuses: List<IssueStatusDTO>,
        private val issueTypes: List<IssueTypeDTO>,
        private val accounts: List<IssueAccountDTO>,
        private val issueDTOS: List<IssueDTO> = emptyList(),
        private val name: String
) {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    }


    fun transform(): IssueTrackerProject {
        val project = IssueTrackerProject(name)
        project.accountRegistry.add(IssueTrackerAccount(
                self = "0",
                name = "Anonymous",
                email = null,
                key = null,
                accountId = null,
                avatarUrl = null,
                project = project
        ))

        addAccountsToProject(project)
        addTaskTypesToProject(project)
        addTaskStatusesToProject(project)

        project.issueRegistry.addAll(issueDTOS.map {
            val detailedTask = DetailedIssue(
                    project = project,
                    id = it.key,
                    self = it.self,
                    summary = it.summary,
                    description = it.description,
                    type = project.issueTypeRegistry.getById(it.typeId)!!,
                    typeName = it.type,
                    status = project.issueStatusRegistry.getById(it.status.id)!!,
                    created = ZonedDateTime.parse(it.created, dateFormatter),
                    updated = ZonedDateTime.parse(it.updated, dateFormatter),
                    creator = getTaskAccount(project, it.creatorId),
                    reporter = it.reporterId?.let { getTaskAccount(project, it) },
                    assignee = it.assigneeId?.let { getTaskAccount(project, it) },
                    priority = it.priority,
                    timeEstimate = it.timeEstimate,
                    timeSpent = it.timeSpent,
                    changes = getChanges(project, it),
                    comments = getComments(project, it),
                    customFields = it.customFields
            )
            detailedTask.commits.forEach { it.issues += detailedTask }
            detailedTask
        })

        linkTasksWithStatuses(project)
        linkTasksWithTypes(project)
        lintTasksWithAuthors(project)
        linkTasksWithSubtasksAndParents(project)

        return project
    }


    private fun getTaskAccount(project: IssueTrackerProject, id: String?): IssueTrackerAccount {
        if (id == null) {
            return project.accountRegistry.getById("0") as IssueTrackerAccount
        }
        return project.accountRegistry.getById(id) as IssueTrackerAccount
    }

    private fun linkTasksWithSubtasksAndParents(project: IssueTrackerProject) {
        issueDTOS.forEach {
            val task = project.issueRegistry.getById(it.key)!!
            if (task is DetailedIssue) {
                it.parent?.let { parentId -> task.parent = project.issueRegistry.getById(parentId) }
                task.subtasks = it.subTasks.mapNotNull { subtaskId -> project.issueRegistry.getById(subtaskId) }
            }
        }
    }

    private fun lintTasksWithAuthors(project: IssueTrackerProject) {
        project.issueRegistry.allDetailedIssues.forEach { task ->
            task.creator.issues += task
            task.reporter?.let { it.issues += task }
            task.assignee?.let { it.issues += task }
            task.changes.forEach { it.account.issues += task }
            task.comments.forEach {
                it.createdBy.issues += task
                it.updatedBy?.let { it.issues += task }
            }
        }
    }

    private fun linkTasksWithTypes(project: IssueTrackerProject) {
        project.issueRegistry.allDetailedIssues.forEach { task ->
            task.type.let { it.issues += task }
        }
    }

    private fun linkTasksWithStatuses(project: IssueTrackerProject) {
        project.issueRegistry.allDetailedIssues.forEach { it.status.issues += it }
    }

    private fun addAccountsToProject(project: IssueTrackerProject) {
        project.accountRegistry.addAll(accounts.map {
            IssueTrackerAccount(
                    self = it.self,
                    email = it.email,
                    accountId = it.accountId,
                    key = it.key,
                    name = it.name,
                    avatarUrl = it.avatarUrl,
                    project = project
            )
        })
    }

    private fun addTaskTypesToProject(project: IssueTrackerProject) {
        project.issueTypeRegistry.addAll(issueTypes.map {
            IssueType(
                    project = project,
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    isSubTask = it.isSubTask
            )
        })
    }

    private fun addTaskStatusesToProject(project: IssueTrackerProject) {
        project.issueStatusCategoryRegistry.addAll(issueStatuses.distinctBy { it.statusCategory }
                .map { it.statusCategory }.map { IssueStatusCategory(project, it.key, it.name) })
        project.issueStatusRegistry.addAll(issueStatuses.map { IssueStatus(project, it.id, it.name, project.issueStatusCategoryRegistry.getById(it.statusCategory.key)!!) })
        project.issueStatusRegistry.all.forEach { it.category.issueStatuses += it }
    }

    private fun getComments(project: IssueTrackerProject, it: IssueDTO) = it.comments.map {
        IssueComment(
                project = project,
                created = getDate(it.created),
                createdBy = getTaskAccount(project, it.userId),
                updated = it.updated?.let { getDate(it) },
                updatedBy = it.updateUserId?.let { getTaskAccount(project, it) },
                body = it.body
        )
    }

    private fun getChanges(project: IssueTrackerProject, it: IssueDTO) = it.changes.map {
        IssueChange(
                project = project,
                id = it.id,
                account = getTaskAccount(project, it.userId),
                created = getDate(it.created),
                changedFields = it.changedFields,
                items = it.items
        )
    }

    private fun getDate(dateAsString: String) =
            ZonedDateTime.parse(dateAsString, dateFormatter)
}
