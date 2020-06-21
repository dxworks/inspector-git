package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.CommitRegistry
import org.dxworks.inspectorgit.registries.DeveloperRegistry
import org.dxworks.inspectorgit.registries.FileRegistry
import org.dxworks.inspectorgit.registries.remote.PullRequestRegistry
import org.dxworks.inspectorgit.registries.remote.RemoteRepoRegistry
import org.dxworks.inspectorgit.registries.remote.SimpleBranchRegistry
import org.dxworks.inspectorgit.registries.task.TaskRegistry
import org.dxworks.inspectorgit.registries.task.TaskStatusCategoryRegistry
import org.dxworks.inspectorgit.registries.task.TaskStatusRegistry
import org.dxworks.inspectorgit.registries.task.TaskTypeRegistry

class Project(val name: String) {
    val accountRegistry = AccountRegistry()
    val developerRegistry = DeveloperRegistry()

    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()


    val taskRegistry = TaskRegistry()
    val taskTypeRegistry = TaskTypeRegistry()
    val taskStatusRegistry = TaskStatusRegistry()
    val taskStatusCategoryRegistry = TaskStatusCategoryRegistry()

    val pullRequestRegistry = PullRequestRegistry()
    val repoRegistry = RemoteRepoRegistry()
    val simpleBranchRegistry = SimpleBranchRegistry()
}
