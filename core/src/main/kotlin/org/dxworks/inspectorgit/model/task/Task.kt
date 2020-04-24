package org.dxworks.inspectorgit.model.task

import org.dxworks.inspectorgit.model.git.Commit

open class Task(val id: String,
                val commits: List<Commit>)