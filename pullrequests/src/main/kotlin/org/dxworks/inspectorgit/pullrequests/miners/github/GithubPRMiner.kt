package org.dxworks.inspectorgit.pullrequests.miners.github

import org.dxworks.inspectorgit.pullrequests.miners.PRMiner
import org.dxworks.inspectorgit.pullrequests.repositories.*
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.client.RequestException
import org.eclipse.egit.github.core.service.IssueService
import org.eclipse.egit.github.core.service.PullRequestService
import org.eclipse.egit.github.core.service.RepositoryService
import org.eclipse.egit.github.core.service.UserService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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

    override fun loadData(username: String, password: String, owner: String, repoName: String) {
        val client = GitHubClient()
        client.setCredentials(username, password);

        //services used by GitHub API
        val repositoryService = RepositoryService(client)
        val userService = UserService(client)
        val pullRequestService = PullRequestService(client)
        val issueService = IssueService(client)

        try {
            val githubRepository = repositoryService.getRepository(owner, repoName)
        } catch (e: RequestException) {
            if (e.message!!.contains("Not Found"))
                LOG.error("Please check the owner and repository name!", e)
            if (e.message!!.contains("Bad credentials"))
                LOG.error("Wrong username or password!", e)
            return;
        }
    }
}