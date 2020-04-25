package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.git.Commit
import java.time.ZonedDateTime

class DetailedTask(id: String,
                   val self: String,
                   val summary: String,
                   val description: String,
                   val type: TaskType?,
                   val typeName: String,
                   val status: String,
                   val created: ZonedDateTime,
                   val updated: ZonedDateTime?,
                   val creator: TaskAccount,
                   val reporter: TaskAccount?,
                   val assignee: TaskAccount?,
                   val priority: String,
                   val changes: List<TaskChange>,
                   val comments: List<TaskComment>,
                   commits: List<Commit>,
                   var parent: Task? = null,
                   var subtasks: List<Task> = emptyList()
) : Task(id, commits) {
    val allCommits: List<Commit>
        get() = commits + subtasks.flatMap { it.commits }
}