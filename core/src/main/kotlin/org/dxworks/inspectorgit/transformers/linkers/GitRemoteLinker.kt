package org.dxworks.inspectorgit.transformers.linkers

import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.model.remote.RemoteGitProject
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class GitRemoteLinker : ProjectLinker<GitProject, RemoteGitProject> {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitRemoteLinker::class.java)
    }

    override fun link(a: GitProject, b: RemoteGitProject) {

        LOG.info("Linking Git ${a.name} with Remote ${b.name}")

        b.simpleBranchRegistry.all
                .forEach { it.commit = a.commitRegistry.getById(it.commitId) }

        b.commitRemoteInfoRegistry.all
                .forEach { a.commitRegistry.getById(it.commitId)?.remoteInfo = it }

        val totalPrs = b.pullRequestRegistry.all.size
        b.pullRequestRegistry.all
                .forEachIndexed { index, pr ->
                    IssueRemoteLinker.LOG.info("Linking PR ${index + 1} / $totalPrs (${(index + 1) * 100 / totalPrs}%)")
                    pr.commits += pr.commitIds
                            .mapNotNull { a.commitRegistry.getById(it) }
                            .onEach { it.pullRequests += pr }
                }
    }

    override fun getKey(): Pair<KClass<GitProject>, KClass<RemoteGitProject>> {
        return Pair(GitProject::class, RemoteGitProject::class)
    }
}
