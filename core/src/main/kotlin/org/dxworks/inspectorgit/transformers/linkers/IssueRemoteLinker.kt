package org.dxworks.inspectorgit.transformers.linkers

import org.dxworks.inspectorgit.model.issuetracker.DetailedIssue
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.model.remote.RemoteGitProject
import org.dxworks.inspectorgit.transformers.getRegexWithWordBoundaryGroups
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class IssueRemoteLinker : ProjectLinker<IssueTrackerProject, RemoteGitProject> {
    companion object {
        val LOG = LoggerFactory.getLogger(IssueRemoteLinker::class.java)
    }

    override fun link(a: IssueTrackerProject, b: RemoteGitProject) {
        val taskPrefixes = a.issueRegistry.allDetailedIssues.map { it.id.substringBefore("-") }.distinct()
        LOG.info("Linking Issue ${a.name} with Remote ${b.name}")
        val totalPrs = b.pullRequestRegistry.all.size

        b.pullRequestRegistry.all
                .forEachIndexed { index, pr ->
                    LOG.info("Linking PR ${index + 1} / $totalPrs (${(index + 1) * 100 / totalPrs}%)")
                    taskPrefixes.forEach {
                        val issues = getRegexWithWordBoundaryGroups("$it-[0-9]+").findAll("${pr.title} ${pr.head.ref} ${pr.body}")
                                .mapNotNull { match -> match.groups[0]?.value }
                                .mapNotNull { issueID -> a.issueRegistry.getById(issueID) }
                                .filterIsInstance<DetailedIssue>()
                                .onEach { issue -> issue.pullRequests += pr }
                                .toList()
                        pr.issues += issues
                    }
                }

        b.simpleBranchRegistry.all
                .forEach { branch ->
                    branch.issue = a.issueRegistry.allDetailedIssues
                            .firstOrNull { getRegexWithWordBoundaryGroups(it.id).containsMatchIn(branch.ref) }
                }
    }

    private fun getLinkedIssues(projects: List<IssueTrackerProject>, content: String) =
            projects.flatMap {
                it.issueRegistry.allDetailedIssues
            }.filter { issue -> getRegexWithWordBoundaryGroups(issue.id).containsMatchIn(content) }

    override fun getKey(): Pair<KClass<IssueTrackerProject>, KClass<RemoteGitProject>> {
        return Pair(IssueTrackerProject::class, RemoteGitProject::class)
    }
}
