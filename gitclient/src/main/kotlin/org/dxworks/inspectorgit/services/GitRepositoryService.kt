package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.gitClient.GitClient
import org.dxworks.inspectorgit.gitClient.dto.GitLogDTO
import org.dxworks.inspectorgit.gitClient.parsers.LogParser
import org.dxworks.inspectorgit.utils.FileSystemUtils
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getRepoFolderPath
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.stereotype.Service


@Service
class GitRepositoryService {
    fun clone(url: String, path: String, branch: String, username: String, password: String) {
        val cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setDirectory(getRepoFolderPath(path).toFile())
                .setCloneAllBranches(true)
                .setBranch(branch)

        cloneCommand.setCredentialsProvider(UsernamePasswordCredentialsProvider(username, password))
        cloneCommand.call()
    }

    fun delete(path: String): Boolean {
        return FileSystemUtils.deleteRepository(path)
    }

    fun getGitLog(path: String): GitLogDTO {
        val gitClient = GitClient(getRepoFolderPath(path))
        return LogParser(gitClient).parse(gitClient.getLogs())
    }
}