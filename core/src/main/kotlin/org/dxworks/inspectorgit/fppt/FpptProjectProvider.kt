package org.dxworks.inspectorgit.fppt

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.jira.TaskImporter
import org.dxworks.inspectorgit.model.ComposedProject
import org.dxworks.inspectorgit.remote.RemoteInfoImporter
import org.dxworks.inspectorgit.transformers.git.GitProjectTransformer
import java.nio.file.Path

class FpptProjectProvider(
        val pathToRepo: Path,
        val pathToIssueTrackingInfo: Path? = null,
        val pathToRemoteInfo: Path? = null
) {
    private var cachedComposedProject: ComposedProject? = null

    val composedProject: ComposedProject
        get() = cachedComposedProject ?: initializeAndGetProject()


    private fun initializeAndGetProject(): ComposedProject {
        val gitClient = GitClient(pathToRepo)
        val gitLogDTO = LogParser(gitClient).parse(gitClient.getLogs())
        val projectName = pathToRepo.toAbsolutePath().normalize().fileName.toString()

        val project = GitProjectTransformer(gitLogDTO, projectName).transform()

        pathToIssueTrackingInfo?.let { TaskImporter().import(it, composedProject = project) }
        pathToRemoteInfo?.let { RemoteInfoImporter().import(it, project) }

        cachedComposedProject = project
        return project
    }
}