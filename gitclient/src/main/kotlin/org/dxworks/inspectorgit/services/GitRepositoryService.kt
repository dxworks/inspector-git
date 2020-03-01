package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.dto.GitLogDTO
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getRepoFolderPath
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
class GitRepositoryService {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitRepositoryService::class.java)
    }

    fun clone(url: String, path: String, branch: String, username: String, password: String) {
        LOG.info("Cloning: $url")
        val cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(getRepoFolderPath(path).toFile())
                .setCloneAllBranches(true)
                .setBranch(branch)

        cloneCommand.setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
        cloneCommand.call()
        LOG.info("Done cloning: $url")
    }

    fun delete(path: String): Boolean {
        return FileSystemUtils.deleteRepository(path)
    }

    fun getGitLog(path: String): GitLogDTO {
        val gitClient = GitClient(getRepoFolderPath(path))
        return LogParser(gitClient).parse(gitClient.getLogs())
    }
}