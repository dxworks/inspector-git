package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.task.DetailedTask
import org.dxworks.inspectorgit.model.task.Task


class TaskRegistry : AbstractRegistry<Task, String>() {
    override fun getID(entity: Task) = entity.id

    val allDetailedTasks: List<DetailedTask>
        get() = all.filter { it is DetailedTask }.map { it as DetailedTask }
}