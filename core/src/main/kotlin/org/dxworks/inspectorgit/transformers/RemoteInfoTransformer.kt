package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.model.git.Commit
import org.dxworks.inspectorgit.model.remote.*
import org.dxworks.inspectorgit.remote.dtos.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RemoteInfoTransformer(private val project: Project, private val remoteInfoDTO: RemoteInfoDTO) {

    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    fun addToProject() {
        val pullRequestDTOs = remoteInfoDTO.pullRequests
        pullRequestDTOs.forEach {
            val pullRequest = PullRequest(
                    it.id,
                    it.title,
                    it.body,
                    getBranch(it.head),
                    getBranch(it.base),
                    getCommits(it.commits),
                    parseDate(it.createdAt),
                    parseDate(it.updatedAt),
                    it.mergedAt?.let { parseDate(it) },
                    it.closedAt?.let { parseDate(it) },
                    it.state,
                    getAccount(it.createdBy),
                    it.mergedBy?.let { getAccount(it) },
                    it.reviews.map { getReview(it) },
                    it.comments,
                    getLinkedTask("${it.title} ${it.head.ref}")
            )
            pullRequest.task?.let { it.pullRequests += pullRequest }
            addPullRequestToAccounts(pullRequest)
            addPullRequestToCommits(pullRequest)
            project.pullRequestRegistry.add(pullRequest)
        }
        remoteInfoDTO.commitInfos.forEach {
            project.commitRegistry.getById(it.id)!!.remoteInfo =
                    CommitRemoteInfo(
                            it.author?.let { getAccount(it) },
                            it.committer?.let { getAccount(it) }
                    )
        }

        project.simpleBranchRegistry.addAll(remoteInfoDTO.branches.map {
            SimpleBranch(project.commitRegistry.getById(it.commit), it.commit, it.name, getLinkedTask(it.name))
        })

    }

    private fun getLinkedTask(content: String) =
            project.taskRegistry.allDetailedTasks
                    .find { getRegexWithWordBoundaryGroups(it.id).containsMatchIn(content) }

    private fun getReview(reviewDTO: ReviewDTO) =
            PRReview(getAccount(reviewDTO.user), reviewDTO.state, reviewDTO.body, parseDate(reviewDTO.date))

    private fun addPullRequestToCommits(pullRequest: PullRequest) {
        (pullRequest.commits + pullRequest.base.commit + pullRequest.head.commit)
                .filterNotNull()
                .distinct()
                .forEach { it.pullRequests += pullRequest }
    }

    private fun addPullRequestToAccounts(pullRequest: PullRequest) {
        val allAuthors = (pullRequest.reviews.map { it.user } + pullRequest.createdBy + pullRequest.mergedBy +
                pullRequest.head.user + pullRequest.head.remoteRepo.owner +
                pullRequest.base.user + pullRequest.base.remoteRepo.owner).filterNotNull().toSet()
        allAuthors.forEach { it.pullRequests += pullRequest }
        pullRequest.createdBy.openedPullRequests += pullRequest;
    }

    private fun getAccount(user: RemoteUserDTO): RemoteGitAccount {
        val byId = project.accountRegistry.getById(user.url)
        return if (byId != null)
            byId as RemoteGitAccount
        else {
            val entity = RemoteGitAccount(user.login, user.url, user.email, user.avatarUrl, user.name, project)
            project.accountRegistry.add(entity)
            entity
        }
    }

    private fun parseDate(date: String): ZonedDateTime {
        return LocalDateTime.parse(date, dateFormatter).atZone(ZoneId.of("Z"))
    }

    private fun getCommits(commits: List<String>): List<Commit> = commits.mapNotNull { getCommit(it) }

    private fun getCommit(id: String) = project.commitRegistry.getById(id)

    private fun getBranch(branch: BranchDTO) = Branch(getCommit(branch.commit),
            branch.commit,
            branch.label,
            branch.ref,
            getAccount(branch.user),
            getRepo(branch.repo),
            getLinkedTask(branch.ref)
    )

    private fun getRepo(remoteRepo: RemoteRepoDTO): RemoteRepo {
        val byId = project.repoRegistry.getById(remoteRepo.id)
        return if (byId != null)
            byId
        else {
            val entity = RemoteRepo(remoteRepo.id, remoteRepo.name, remoteRepo.fullName, getAccount(remoteRepo.owner))
            project.repoRegistry.add(entity)
            entity
        }
    }
}