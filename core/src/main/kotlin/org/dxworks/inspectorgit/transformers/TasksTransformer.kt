package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.fppt.jira.dtos.TaskAccountDTO
import org.dxworks.inspectorgit.fppt.jira.dtos.TaskDTO
import org.dxworks.inspectorgit.fppt.jira.dtos.TaskTypeDTO
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.task.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TasksTransformer(
        private val project: Project,
        val taskTypes: Map<String, TaskTypeDTO>,
        val accounts: Map<String, TaskAccountDTO>,
        private val taskDTOs: List<TaskDTO> = emptyList(),
        private val taskPrefixes: List<String> = emptyList()
) {
    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    }

    fun addToProject() {
        val taskPrefixes = (taskDTOs.map { it.id.substring(0, it.id.indexOf("-")) } + this.taskPrefixes).distinct()
        if (taskPrefixes.isEmpty())
            return

        addAccountsToProject()
        addTaskTypesToProject()


        val taskRegexList = taskPrefixes.map { getTaskRegex(it) }
        val smartCommits = project.commitRegistry.all
                .filter { taskRegexList.any { taskRegex -> taskRegex.containsMatchIn(it.message) } }
        val taskIdToSmartCommitMap = mapOfCommitsByTaskId(smartCommits, taskRegexList)


        taskIdToSmartCommitMap.forEach { (id, commits) -> commits.forEach { it.taskIds = it.taskIds + id } }

        project.taskRegistry.addAll(taskDTOs.map {
            DetailedTask(
                    id = it.id,
                    self = it.self,
                    summary = it.summary,
                    description = it.description,
                    type = project.taskTypeRegistry.getById(it.typeId),
                    typeName = it.type,
                    status = it.status,
                    created = ZonedDateTime.parse(it.created, dateFormatter),
                    updated = ZonedDateTime.parse(it.updated, dateFormatter),
                    changes = getChanges(it),
                    comments = getComments(it),
                    commits = taskIdToSmartCommitMap.remove(it.id) ?: emptyList()
            )
        })

        linkTasksWithTypes()

        project.taskRegistry.addAll(taskIdToSmartCommitMap.map { Task(it.key, it.value) })

        linkTasksWithSubtasksAndParents()
    }

    private fun linkTasksWithSubtasksAndParents() {
        taskDTOs.forEach {
            val task = project.taskRegistry.getById(it.id)!!
            if (task is DetailedTask) {
                it.parentId?.let { parentId -> task.parent = project.taskRegistry.getById(parentId) }
                task.subtasks = it.subTasks.mapNotNull { subtaskId -> project.taskRegistry.getById(subtaskId) }
            }
        }
    }

    private fun linkTasksWithTypes() {
        project.taskRegistry.all.filter { it is DetailedTask }.map { it as DetailedTask }.forEach { task ->
            task.type?.let { it.tasks += task }
        }
    }

    private fun addAccountsToProject() {
        project.accountRegistry.addAll(accounts.values.map {
            TaskAccount(
                    id = it.id,
                    name = it.name,
                    avatarUrl = it.avatarUrl,
                    project = project
            )
        })
    }

    private fun addTaskTypesToProject() {
        project.taskTypeRegistry.addAll(taskTypes.values.map {
            TaskType(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    isSubTask = it.isSubTask
            )
        })
    }

    private fun getComments(it: TaskDTO) = it.comments.map {
        TaskComment(
                created = getDate(it.created),
                createdBy = project.accountRegistry.getById(it.userId) as TaskAccount,
                updated = getDate(it.updated),
                updatedBy = project.accountRegistry.getById(it.updateUserId) as TaskAccount,
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
