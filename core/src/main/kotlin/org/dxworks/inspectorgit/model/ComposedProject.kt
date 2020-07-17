package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.DeveloperRegistry
import org.dxworks.inspectorgit.registries.git.CommitRegistry
import org.dxworks.inspectorgit.registries.git.FileRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueStatusCategoryRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueStatusRegistry
import org.dxworks.inspectorgit.registries.issuetracker.IssueTypeRegistry
import org.dxworks.inspectorgit.registries.remote.PullRequestRegistry
import org.dxworks.inspectorgit.registries.remote.RemoteRepoRegistry
import org.dxworks.inspectorgit.registries.remote.SimpleBranchRegistry

class ComposedProject(val name: String) {
    val accountRegistry = AccountRegistry()
    val developerRegistry = DeveloperRegistry()

    val commitRegistry = CommitRegistry()
    val fileRegistry = FileRegistry()


    val issueRegistry = IssueRegistry()
    val issueTypeRegistry = IssueTypeRegistry()
    val issueStatusRegistry = IssueStatusRegistry()
    val issueStatusCategoryRegistry = IssueStatusCategoryRegistry()

    val pullRequestRegistry = PullRequestRegistry()
    val repoRegistry = RemoteRepoRegistry()
    val simpleBranchRegistry = SimpleBranchRegistry()
}
