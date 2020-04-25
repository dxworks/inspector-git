package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.*

class Project(val name: String) {
    val accountRegistry = AccountRegistry()
    val developerRegistry = DeveloperRegistry()

    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()


    val taskRegistry = TaskRegistry()
    val taskTypeRegistry = TaskTypeRegistry()

    val pullRequestRegistry = PullRequestRegistry()
}
