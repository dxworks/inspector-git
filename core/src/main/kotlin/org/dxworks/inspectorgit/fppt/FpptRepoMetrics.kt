package org.dxworks.inspectorgit.fppt

import org.dxworks.inspectorgit.model.Project

fun fpptRepoMetrics(project: Project): Map<String, Any?> {
    val allCommits = project.commitRegistry.all
    val changesPerCommit = allCommits.map { it.changes.size }
    return mapOf(
            "commits" to allCommits.size,
            "pullRequests" to project.pullRequestRegistry.all.size,
            "averageChangesPerCommit" to changesPerCommit.average(),
            "maxChangesPerCommit" to changesPerCommit.max(),
            "minChangesPerCommit" to changesPerCommit.min(),
            "firstCommitDate" to allCommits.map { it.committerDate }.min(),
            "lastCommitDate" to allCommits.map { it.committerDate }.max()
    )
}