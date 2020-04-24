package org.dxworks.inspectorgit.registries

import org.dxworks.inspectorgit.model.task.Task


class TaskRegistry : AbstractRegistry<Task, String>() {
    override fun getID(entity: Task) = entity.id
}