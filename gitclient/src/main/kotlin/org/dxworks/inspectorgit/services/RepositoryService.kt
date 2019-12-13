package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getRepoFolderPath
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.stereotype.Service


@Service
class RepositoryService {
    fun cloneRepository(url: String, repoName: String, branch: String, username: String, password: String) {
        val cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(getRepoFolderPath(repoName).toFile())
                .setCloneAllBranches(true)
                .setBranch(branch)

        cloneCommand.setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
        cloneCommand.call()
    }
}