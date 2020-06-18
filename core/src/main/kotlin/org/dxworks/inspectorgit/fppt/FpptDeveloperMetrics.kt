package org.dxworks.inspectorgit.fppt

import org.dxworks.inspectorgit.model.Account
import org.dxworks.inspectorgit.model.Developer
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.GitAccount
import org.dxworks.inspectorgit.model.remote.RemoteGitAccount

fun fpptDeveloperMetrics(project: Project): Any {
    val allDevelopers = project.developerRegistry.all
    return allDevelopers.map {
        val commits = getTypeAccounts<GitAccount>(it).flatMap { it.commits }
        val changesPerCommit = commits.map { it.changes.size }
        val hunksPerCommit = commits.map { it.changes.sumBy { it.hunks.size } }
        val messageSizePerCommit = commits.map { it.message.length }
        it.name to mapOf(
                "commits" to commits.size,
                "pullRequests" to getTypeAccounts<RemoteGitAccount>(it).sumBy { it.openedPullRequests.size },
                "averageChangesPerCommit" to changesPerCommit.average(),
                "maxChangesPerCommit" to changesPerCommit.max(),
                "minChangesPerCommit" to changesPerCommit.min(),
                "averageHunksPerCommit" to hunksPerCommit.average(),
                "maxHunksPerCommit" to hunksPerCommit.max(),
                "minHunksPerCommit" to hunksPerCommit.min(),
                "averageMessageLengthPerCommit" to messageSizePerCommit.average(),
                "maxMessageLengthPerCommit" to messageSizePerCommit.max(),
                "minMessageLengthPerCommit" to messageSizePerCommit.min(),
                "firstCommitDate" to commits.map { it.committerDate }.min(),
                "lastCommitDate" to commits.map { it.committerDate }.max()
        )
    }.toMap()
}

private inline fun <reified T : Account> getTypeAccounts(it: Developer) =
        it.accounts.filterIsInstance<T>().map { it }