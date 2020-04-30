package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Project

class TaskStatusCategory(
        val project: Project,
        val key: String,
        val name: String,
        var taskStatuses: List<TaskStatus> = emptyList()
) {
    companion object {
        public val new = "new"
        public val indeterminate = "indeterminate"
        public val done = "done"
    }
}
