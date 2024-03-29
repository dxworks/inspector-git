package org.dxworks.inspectorgit.model.issuetracker

import org.dxworks.inspectorgit.model.git.Commit

open class Issue(val id: String,
                 val project: IssueTrackerProject,
                 var commits: Set<Commit>
)