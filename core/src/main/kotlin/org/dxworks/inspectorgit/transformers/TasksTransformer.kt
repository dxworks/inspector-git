package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.jira.dtos.TaskAccountDTO
import org.dxworks.inspectorgit.jira.dtos.TaskDTO
import org.dxworks.inspectorgit.jira.dtos.TaskStatusDTO
import org.dxworks.inspectorgit.jira.dtos.TaskTypeDTO
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.task.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TasksTransformer(
        private val project: Project,
        val issueStatuses: List<TaskStatusDTO>,
        val taskTypes: List<TaskTypeDTO>,
        val accounts: List<TaskAccountDTO>,
        private val taskDTOs: List<TaskDTO> = emptyList(),
        private val taskPrefixes: List<String> = emptyList()
) {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    }

    fun addToProject() {
        val taskPrefixes = (taskDTOs.map { it.key.substring(0, it.key.indexOf("-")) } + this.taskPrefixes).distinct()
        if (taskPrefixes.isEmpty())
            return

        addAccountsToProject()
        addTaskTypesToProject()
        addTaskStatusesToProject()


        val taskRegexList = taskPrefixes.map { getTaskRegex(it) }
        val smartCommits = project.commitRegistry.all
                .filter { taskRegexList.any { taskRegex -> taskRegex.containsMatchIn(it.message) } }
        val taskIdToSmartCommitMap = mapOfCommitsByTaskId(smartCommits, taskRegexList)


        taskIdToSmartCommitMap.forEach { (id, commits) -> commits.forEach { it.taskIds = it.taskIds + id } }

        project.taskRegistry.addAll(taskDTOs.map {
            DetailedTask(
                    id = it.key,
                    self = it.self,
                    summary = it.summary,
                    description = it.description,
                    type = project.taskTypeRegistry.getById(it.typeId),
                    typeName = it.type,
                    status = project.taskStatusRegistry.getById(it.status.id)!!,
                    created = ZonedDateTime.parse(it.created, dateFormatter),
                    updated = it.updated?.let { ZonedDateTime.parse(it, dateFormatter) },
                    creator = getTaskAccount(it.creatorId),
                    reporter = it.reporterId?.let { getTaskAccount(it) },
                    assignee = it.assigneeId?.let { getTaskAccount(it) },
                    priority = it.priority,
                    changes = getChanges(it),
                    comments = getComments(it),
                    commits = taskIdToSmartCommitMap.remove(it.key) ?: emptyList()
            )
        })

        linkTasksWithStatuses()
        linkTasksWithTypes()
        lintTasksWithAuthors()

        project.taskRegistry.addAll(taskIdToSmartCommitMap.map { Task(it.key, it.value) })

        linkTasksWithSubtasksAndParents()
    }


    private fun getTaskAccount(id: String) =
            project.accountRegistry.getById(id) as TaskAccount

    private fun linkTasksWithSubtasksAndParents() {
        taskDTOs.forEach {
            val task = project.taskRegistry.getById(it.key)!!
            if (task is DetailedTask) {
                it.parent?.let { parentId -> task.parent = project.taskRegistry.getById(parentId) }
                task.subtasks = it.subTasks.mapNotNull { subtaskId -> project.taskRegistry.getById(subtaskId) }
            }
        }
    }

    private fun lintTasksWithAuthors() {
        project.taskRegistry.allDetailedTasks.forEach { task ->
            task.creator.tasks += task
            task.reporter?.let { it.tasks += task }
            task.assignee?.let { it.tasks += task }
            task.changes.forEach { it.account.tasks += task }
            task.comments.forEach {
                it.createdBy.tasks += task
                it.updatedBy?.let { it.tasks += task }
            }
        }
    }

    private fun linkTasksWithTypes() {
        project.taskRegistry.allDetailedTasks.forEach { task ->
            task.type?.let { it.tasks += task }
        }
    }

    private fun linkTasksWithStatuses() {
        project.taskRegistry.allDetailedTasks.forEach { it.status.tasks += it }
    }

    private fun addAccountsToProject() {
        project.accountRegistry.addAll(accounts.map {
            TaskAccount(
                    id = it.id,
                    name = it.name,
                    avatarUrl = it.avatarUrl,
                    project = project
            )
        })
    }

    private fun addTaskTypesToProject() {
        project.taskTypeRegistry.addAll(taskTypes.map {
            TaskType(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    isSubTask = it.isSubTask
            )
        })
    }

    private fun addTaskStatusesToProject() {
        project.taskStatusCategoryRegistry.addAll(issueStatuses.distinctBy { it.statusCategory }
                .map { it.statusCategory }.map { TaskStatusCategory(it.key, it.name) })
        project.taskStatusRegistry.addAll(issueStatuses.map { TaskStatus(it.id, it.name, project.taskStatusCategoryRegistry.getById(it.statusCategory.key)!!) })
        project.taskStatusRegistry.all.forEach { it.category.taskStatuses += it }
    }

    private fun getComments(it: TaskDTO) = it.comments.map {
        TaskComment(
                created = getDate(it.created),
                createdBy = getTaskAccount(it.userId),
                updated = it.updated?.let { getDate(it) },
                updatedBy = it.updateUserId?.let { getTaskAccount(it) },
                body = it.body
        )
    }

    private fun getChanges(it: TaskDTO) = it.changes.map {
        TaskChange(
                id = it.id,
                account = project.accountRegistry.getById(it.userId) as TaskAccount,
                created = getDate(it.created),
                changedFields = it.changedFields,
                items = it.items
        )
    }

    private fun getDate(dateAsString: String) =
            ZonedDateTime.parse(dateAsString, dateFormatter)

    private fun getTaskRegex(prefix: String) = "(\\b|'|\")$prefix-\\d+(\\b|\"|')".toRegex()

    private fun mapOfCommitsByTaskId(allSmartCommits: List<Commit>, taskRegexList: List<Regex>): MutableMap<String, List<Commit>> {
        val allTaskIds = allSmartCommits.flatMap { taskRegexList.flatMap { taskRegex -> taskRegex.findAll(it.message).toList().map { it.value } } }
        return allTaskIds.map { id -> id to allSmartCommits.filter { it.message.contains(id) } }.toMap().toMutableMap()
    }
}
