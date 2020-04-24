package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.task.TaskType

class TaskTypeRegistry : AbstractRegistry<TaskType, String>() {
    override fun getID(entity: TaskType) = entity.id
}