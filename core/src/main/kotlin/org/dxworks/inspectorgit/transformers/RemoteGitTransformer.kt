package org.dxworks.inspectorgit.transformers

import org.dxworks.inspectorgit.model.remote.*
import org.dxworks.inspectorgit.remote.dtos.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RemoteGitTransformer(private val remoteInfoDTO: RemoteInfoDTO, private val name: String) {

    companion object {
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    }

    fun transform(): RemoteGitProject {
        val project = RemoteGitProject(name)
        val pullRequestDTOs = remoteInfoDTO.pullRequests
        pullRequestDTOs.forEach {
            val pullRequest = PullRequest(
                    id = it.id,
                    title = it.title,
                    body = it.body,
                    head = getBranch(project, it.head),
                    base = getBranch(project, it.base),
                    commitIds = it.commits,
                    createdAt = parseDate(it.createdAt),
                    updatedAt = parseDate(it.updatedAt),
                    mergedAt = it.mergedAt?.let { parseDate(it) },
                    closedAt = it.closedAt?.let { parseDate(it) },
                    state = it.state,
                    createdBy = getAccount(project, it.createdBy),
                    mergedBy = it.mergedBy?.let { getAccount(project, it) },
                    reviews = it.reviews.map { getReview(project, it) },
                    comments = it.comments,
                    project = project
            )
            addPullRequestToAccounts(pullRequest)
            project.pullRequestRegistry.add(pullRequest)
        }
        project.commitRemoteInfoRegistry.addAll(
                remoteInfoDTO.commitInfos.map {
                    CommitRemoteInfo(
                            it.id,
                            it.author?.let { getAccount(project, it) },
                            it.committer?.let { getAccount(project, it) }
                    )
                })

        project.simpleBranchRegistry.addAll(remoteInfoDTO.branches.map {
            SimpleBranch(null, it.commit, it.name, null)
        })
        return project
    }


    private fun getReview(project: RemoteGitProject, reviewDTO: ReviewDTO) =
            PRReview(getAccount(project, reviewDTO.user), reviewDTO.state, reviewDTO.body, parseDate(reviewDTO.date))


    private fun addPullRequestToAccounts(pullRequest: PullRequest) {
        val allAuthors = (pullRequest.reviews.map { it.user } + pullRequest.createdBy + pullRequest.mergedBy +
                pullRequest.head.user + pullRequest.head.remoteRepo.owner +
                pullRequest.base.user + pullRequest.base.remoteRepo.owner).filterNotNull().toSet()
        allAuthors.forEach { it.pullRequests += pullRequest }
        pullRequest.createdBy.openedPullRequests += pullRequest;
    }

    private fun getAccount(project: RemoteGitProject, user: RemoteUserDTO): RemoteGitAccount {
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

    private fun getBranch(project: RemoteGitProject, branch: BranchDTO) = Branch(
            branch.commit,
            branch.label,
            branch.ref,
            getAccount(project, branch.user),
            getRepo(project, branch.repo)
    )

    private fun getRepo(project: RemoteGitProject, remoteRepo: RemoteRepoDTO): RemoteRepo {
        val byId = project.repoRegistry.getById(remoteRepo.id)
        return if (byId != null)
            byId
        else {
            val entity = RemoteRepo(remoteRepo.id, remoteRepo.name, remoteRepo.fullName, getAccount(project, remoteRepo.owner))
            project.repoRegistry.add(entity)
            entity
        }
    }
}