package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit

open class Task(val id: String,
                val project: Project,
                val commits: List<Commit>)