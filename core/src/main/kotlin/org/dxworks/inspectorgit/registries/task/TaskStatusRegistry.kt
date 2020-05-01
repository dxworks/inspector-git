package org.dxworks.inspectorgit.registries.task

import org.dxworks.inspectorgit.model.task.TaskStatus
import org.dxworks.inspectorgit.model.task.TaskStatusCategory
import org.dxworks.inspectorgit.registries.AbstractRegistry


class TaskStatusRegistry : AbstractRegistry<TaskStatus, String>() {
    override fun getID(entity: TaskStatus) = entity.id
    fun isNew(id: String) = hasCategory(id, TaskStatusCategory.new)
    fun isIndeterminate(id: String) = hasCategory(id, TaskStatusCategory.indeterminate)
    fun isDone(id: String) = hasCategory(id, TaskStatusCategory.done)

    private fun hasCategory(id: String, categoryKey: String) =
            getById(id)?.category?.key?.equals(categoryKey) ?: false
}