package org.dxworks.inspectorgit.model.remote

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.registries.AccountRegistry
import org.dxworks.inspectorgit.registries.remote.CommitRemoteInfoRegistry
import org.dxworks.inspectorgit.registries.remote.PullRequestRegistry
import org.dxworks.inspectorgit.registries.remote.RemoteRepoRegistry
import org.dxworks.inspectorgit.registries.remote.SimpleBranchRegistry

class RemoteGitProject(override val name: String) : Project() {
    override val accountRegistry = AccountRegistry()
    val pullRequestRegistry = PullRequestRegistry()
    val repoRegistry = RemoteRepoRegistry()
    val simpleBranchRegistry = SimpleBranchRegistry()
    val commitRemoteInfoRegistry = CommitRemoteInfoRegistry()
}
