package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getRepoFolderPath
import org.slf4j.LoggerFactory

class GitRepositoryService {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitRepositoryService::class.java)
    }

    fun clone(url: String, path: String, branch: String, username: String, password: String) {
        GitClient(getRepoFolderPath(path)).clone(url, username, password)?.forEach { LOG.info(it) }
        GitClient(getRepoFolderPath(path)).checkout(branch)?.forEach { LOG.info(it) }

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