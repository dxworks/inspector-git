package org.dxworks.inspectorgit.registries.task

import org.dxworks.inspectorgit.model.task.TaskStatusCategory
import org.dxworks.inspectorgit.registries.AbstractRegistry

class TaskStatusCategoryRegistry : AbstractRegistry<TaskStatusCategory, String>() {
    override fun getID(entity: TaskStatusCategory) = entity.key
}
