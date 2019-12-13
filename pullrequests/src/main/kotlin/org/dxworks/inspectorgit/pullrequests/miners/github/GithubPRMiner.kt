package org.dxworks.inspectorgit.pullrequests.miners.github

import org.dxworks.inspectorgit.pullrequests.entities.PullRequestEntity
import org.dxworks.inspectorgit.pullrequests.miners.PRMiner
import org.dxworks.inspectorgit.pullrequests.miners.PRMinerOptions
import org.dxworks.inspectorgit.pullrequests.repositories.*
import org.eclipse.egit.github.core.PullRequest
import org.eclipse.egit.github.core.Repository
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.client.RequestException
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.service.RepositoryService
import org.eclipse.egit.github.core.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Service
class GithubPRMiner(private val prProjectRepository: PRProjectRepository,
                    private val pullRequestRepository: PullRequestRepository,
                    private val developerRepository: DeveloperRepository,
                    private val commitRepository: CommitRepository,
                    private val commentRepository: CommentRepository,
                    private val fileRepository: FileRepository) : PRMiner() {
    companion object {
        val LOG: Logger = LoggerFactory.getLogger(GithubPRMiner::class.java)
    }

    lateinit var userService: UserService
    lateinit var pullRequestService: CustomPullRequestService
    lateinit var issueService: IssueService

    override fun saveToDatabase(options: PRMinerOptions) {
        val client = GitHubClient()
        client.setCredentials(options.username, options.password);

        //services used by GitHub API
        userService = UserService(client)
        issueService = IssueService(client)
        pullRequestService = CustomPullRequestService(client)
        val repositoryService = RepositoryService(client)

        try {
            val githubRepository = repositoryService.getRepository(options.owner, options.repository)
            saveToDatabase(githubRepository, options.newerThan)
        } catch (e: RequestException) {
            if (e.message!!.contains("Not Found"))
                LOG.error("Please check the owner and repository name!", e)
            if (e.message!!.contains("Bad credentials"))
                LOG.error("Wrong username or password!", e)
            return
        }
    }

    private fun saveToDatabase(repository: Repository, newerThan: Date?) {
        getAndSavePullRequests(pullRequestService, repository, newerThan)
    }

    private fun getAndSavePullRequests(pullRequestService: CustomPullRequestService, repository: Repository, newerThan: Date?): List<PullRequest?> {
        val params = mapOf(Pair("state", "all"), Pair("sort", "updated"), Pair("direction", "desc"))
        val iterator = pullRequestService.pagePullRequests(repository, params)!!

        val pullRequests: MutableList<PullRequest?> = ArrayList()

        while (iterator.hasNext()) {
            val currentPullRequests = iterator.next()

            if (newerThan != null) {
                val updatedPullRequests = currentPullRequests.filter { it!!.updatedAt < newerThan }
                pullRequests.addAll(updatedPullRequests)
                saveAll(updatedPullRequests)
                if (anyWereFiltered(updatedPullRequests, currentPullRequests))
                    break
            } else {
                pullRequests.addAll(currentPullRequests)
                saveAll(currentPullRequests)
            }
        }
        return pullRequests
    }

    private fun saveAll(pullRequests: Collection<PullRequest?>) {
//        this.pullRequestRepository.saveAll(pullRequests.map {  })
    }

    private fun anyWereFiltered(updatedPullRequests: Collection<PullRequest?>, currentPullRequests: Collection<PullRequest?>) =
            updatedPullRequests.size < currentPullRequests.size
}