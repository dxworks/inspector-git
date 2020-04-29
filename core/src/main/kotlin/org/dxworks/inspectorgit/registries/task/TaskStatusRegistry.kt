package org.dxworks.inspectorgit.registries.task

import org.dxworks.inspectorgit.model.task.TaskStatus
import org.dxworks.inspectorgit.registries.AbstractRegistry


class TaskStatusRegistry : AbstractRegistry<TaskStatus, String>() {
    override fun getID(entity: TaskStatus) = entity.id
}