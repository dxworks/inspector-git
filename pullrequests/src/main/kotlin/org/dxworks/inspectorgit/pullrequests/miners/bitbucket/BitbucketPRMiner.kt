package org.dxworks.inspectorgit.pullrequests.miners.bitbucket

import org.dxworks.inspectorgit.pullrequests.miners.PRMiner
import org.dxworks.inspectorgit.pullrequests.miners.bitbucket.dto.PullRequestListDTO
import org.dxworks.inspectorgit.pullrequests.repositories.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class BitbucketPRMiner(private val prProjectRepository: PRProjectRepository,
                       private val pullRequestRepository: PullRequestRepository,
                       private val developerRepository: DeveloperRepository,
                       private val commitRepository: CommitRepository,
                       private val commentRepository: CommentRepository,
                       private val fileRepository: FileRepository) : PRMiner() {
    companion object {
        val LOG = LoggerFactory.getLogger(BitbucketPRMiner::class.java)
    }

    private fun getRepoUrl(owner: String, repoName: String) =
            "https://api.bitbucket.org/2.0/repositories/$owner/$repoName/"

    fun getPagedPullRequestsUrl(owner: String, repoName: String, page: Int) =
            "${getRepoUrl(owner, repoName)}pullrequests?&page=$page"

    @Async
    override fun loadData(username: String, password: String, owner: String, repoName: String) {
        val restTemplate = RestTemplate()
        getPullRequests(restTemplate, getPagedPullRequestsUrl(owner, repoName, 0), username, password)
    }

    private fun getPullRequests(restTemplate: RestTemplate, url: String, username: String, password: String): PullRequestListDTO? {
        try {
            return restTemplate.exchange(url, HttpMethod.GET,
                    HttpEntity<PullRequestListDTO>(createHeaders(username, password)), PullRequestListDTO::class.java)
                    .body
        } catch (e: HttpClientErrorException.Unauthorized) {
            LOG.error("Wrong username or password!", e)
        } catch (e: HttpClientErrorException.NotFound) {
            LOG.error("Pull request not found! Please check owner and repository name.", e)
        }
        return null
    }

}