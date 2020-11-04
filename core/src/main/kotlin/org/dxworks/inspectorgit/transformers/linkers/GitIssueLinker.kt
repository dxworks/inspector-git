package org.dxworks.inspectorgit.transformers.linkers

import org.dxworks.inspectorgit.model.git.GitProject
import org.dxworks.inspectorgit.model.issuetracker.IssueTrackerProject
import org.dxworks.inspectorgit.transformers.getRegexWithWordBoundaryGroups
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class GitIssueLinker : ProjectLinker<GitProject, IssueTrackerProject> {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitIssueLinker::class.java)
    }

    override fun link(a: GitProject, b: IssueTrackerProject) {
        LOG.info("Linking Git ${a.name} with Issues ${b.name}")

        val taskPrefixes = b.issueRegistry.allDetailedIssues.map { it.id.substringBefore("-") }.distinct()

        val totalCommits = a.commitRegistry.all.size
        a.commitRegistry.all.forEachIndexed { index, commit ->
            LOG.info("Linking commit ${index + 1} / $totalCommits (${(index + 1) * 100 / totalCommits}%)")
            taskPrefixes.forEach { prefix ->
                val issues = getRegexWithWordBoundaryGroups("$prefix-[0-9]+").findAll(commit.message)
                        .mapNotNull { it.groups[0]?.value }
                        .mapNotNull { issueID -> b.issueRegistry.getById(issueID) }
                        .onEach { it.commits += commit }
                        .toList()
                commit.issues += issues
            }
        }
    }

    override fun getKey(): Pair<KClass<GitProject>, KClass<IssueTrackerProject>> {
        return Pair(GitProject::class, IssueTrackerProject::class)
    }
}
