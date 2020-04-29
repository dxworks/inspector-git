package org.dxworks.inspectorgit.registries.task

import org.dxworks.inspectorgit.model.task.DetailedTask
import org.dxworks.inspectorgit.model.task.Task
import org.dxworks.inspectorgit.registries.AbstractRegistry


class TaskRegistry : AbstractRegistry<Task, String>() {
    override fun getID(entity: Task) = entity.id

    val allDetailedTasks: List<DetailedTask>
        get() = all.filter { it is DetailedTask }.map { it as DetailedTask }
}