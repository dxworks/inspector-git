package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.fppt.dtos.task.TaskDTO
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.task.DetailedTask
import org.dxworks.inspectorgit.model.task.Task
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class TasksTransformer(private val project: Project, private val taskDTOs: List<TaskDTO> = emptyList(), private val taskPrefixes: List<String> = emptyList()) {
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("")

    fun addToProject() {
        val taskPrefixes = (taskDTOs.map { it.id.substring(0, it.id.indexOf("-")) } + this.taskPrefixes).distinct()
        if (taskPrefixes.isEmpty())
            return


        val taskRegexList = taskPrefixes.map { getTaskRegex(it) }
        val smartCommits = project.commitRegistry.all
                .filter { taskRegexList.any { taskRegex -> taskRegex.containsMatchIn(it.message) } }
        val taskIdToSmartCommitMap = mapOfCommitsByTaskId(smartCommits, taskRegexList)


        taskIdToSmartCommitMap.forEach { (id, commits) -> commits.forEach { it.taskIds = it.taskIds + id } }

        project.taskRegistry.addAll(taskDTOs.map {
            DetailedTask(
                    id = it.id,
                    summary = it.summary,
                    description = it.description,
                    type = it.type,
                    status = it.status,
                    created = ZonedDateTime.parse(it.created, dateFormatter),
                    updated = ZonedDateTime.parse(it.updated, dateFormatter),
                    commits = taskIdToSmartCommitMap.remove(it.id) ?: emptyList()
            )
        })

        project.taskRegistry.addAll(taskIdToSmartCommitMap.map { Task(it.key, it.value) })

        taskDTOs.forEach {
            val task = project.taskRegistry.getById(it.id)!!
            if (task is DetailedTask) {
                it.parentId?.let { parentId -> task.parent = project.taskRegistry.getById(parentId) }
                task.subtasks = it.subtaskIds.mapNotNull { subtaskId -> project.taskRegistry.getById(subtaskId) }
            }
        }
    }

    private fun getTaskRegex(prefix: String) = "(\\b|'|\")$prefix-\\d+(\\b|\"|')".toRegex()

    private fun mapOfCommitsByTaskId(allSmartCommits: List<Commit>, taskRegexList: List<Regex>): MutableMap<String, List<Commit>> {
        val allTaskIds = allSmartCommits.flatMap { taskRegexList.flatMap { taskRegex -> taskRegex.findAll(it.message).toList().map { it.value } } }
        return allTaskIds.map { id -> id to allSmartCommits.filter { it.message.contains(id) } }.toMap().toMutableMap()
    }
}