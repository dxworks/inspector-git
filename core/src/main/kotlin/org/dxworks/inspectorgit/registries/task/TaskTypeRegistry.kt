package org.dxworks.inspectorgit.registries.task

import org.dxworks.inspectorgit.model.task.TaskType
import org.dxworks.inspectorgit.registries.AbstractRegistry

class TaskTypeRegistry : AbstractRegistry<TaskType, String>() {
    override fun getID(entity: TaskType) = entity.id
}