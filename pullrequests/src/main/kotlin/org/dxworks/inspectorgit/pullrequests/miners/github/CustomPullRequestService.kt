package org.dxworks.inspectorgit.pullrequests.miners.github

import com.google.gson.reflect.TypeToken
import org.eclipse.egit.github.core.IRepositoryIdProvider
import org.eclipse.egit.github.core.PullRequest
import org.eclipse.egit.github.core.client.GitHubClient
import org.eclipse.egit.github.core.client.NoSuchPageException
import org.eclipse.egit.github.core.client.PageIterator
import org.eclipse.egit.github.core.client.PagedRequest
import org.eclipse.egit.github.core.service.PullRequestService
import java.io.IOException
import java.util.*

class CustomPullRequestService(client: GitHubClient) : PullRequestService(client) {

    fun getPullRequests(repository: IRepositoryIdProvider, params: Map<String, String>): List<PullRequest?>? {
        return this.getAll(this.pagePullRequests(repository, params))
    }

    fun pagePullRequests(repository: IRepositoryIdProvider, params: Map<String, String>): PageIterator<PullRequest?>? {
        return this.pagePullRequests(repository, params, 100)
    }

    fun pagePullRequests(repository: IRepositoryIdProvider, params: Map<String, String>, size: Int): PageIterator<PullRequest?>? {
        return this.pagePullRequests(repository, params, 1, size)
    }

    fun pagePullRequests(repository: IRepositoryIdProvider, params: Map<String, String>, start: Int, size: Int): PageIterator<PullRequest?>? {
        val request = createPullsRequest(repository, params, start, size)
        return createPageIterator(request)
    }

    fun createPullsRequest(provider: IRepositoryIdProvider, params: Map<String, String>, start: Int, size: Int): PagedRequest<PullRequest>? {
        val id = getId(provider)
        val uri = StringBuilder("/repos")
        uri.append('/').append(id)
        uri.append("/pulls")
        val request = this.createPagedRequest<PullRequest>(start, size)
        request.setUri(uri)
        request.params = params
        request.type = object : TypeToken<List<PullRequest?>?>() {}.type
        return request
    }
}