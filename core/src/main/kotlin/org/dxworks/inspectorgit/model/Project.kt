package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.*

class Project(val name: String) {
    val accountRegistry = AccountRegistry()
    val developerRegistry = GitAccountRegistry()
    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()
    val taskRegistry = TaskRegistry()
    val pullRequestRegistry = PullRequestRegistry()
}
