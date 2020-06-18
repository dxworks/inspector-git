package org.dxworks.inspectorgit.fppt

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.jira.TaskImporter
import org.dxworks.inspectorgit.model.Project
import org.dxworks.inspectorgit.remote.RemoteInfoImporter
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import java.nio.file.Path

class FpptProjectProvider(
        val pathToRepo: Path,
        val pathToIssueTrackingInfo: Path? = null,
        val pathToRemoteInfo: Path? = null
) {
    companion object {
        private var staticProject: Project? = null
    }

    val project: Project
        get() = staticProject ?: initializeAndGetProject()


    private fun initializeAndGetProject(): Project {
        val gitClient = GitClient(pathToRepo)
        val gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())
        val projectName = pathToRepo.toAbsolutePath().normalize().fileName.toString()

        val project = ProjectTransformer(gitLogDTO, projectName).transform()

        pathToIssueTrackingInfo?.let { TaskImporter().import(it, project = project) }
        pathToRemoteInfo?.let { RemoteInfoImporter().import(it, project) }

        staticProject = project
        return project
    }
}