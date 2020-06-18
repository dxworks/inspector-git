package org.dxworks.inspectorgit.fppt

import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.test.assertNotNull

internal class FpptProjectProviderTest {

    @Test
    fun getProject() {
        val base = "C:\\Users\\dnagy\\Documents\\personal\\fppt\\project"
        val pathToRepo = Paths.get("$base\\Music-Events-Application")
        val pathToIssueTrackingInfo = Paths.get("$base\\mea-tasks.json")
        val pathToRemoteInfo = Paths.get("$base\\remote-export.json")
        val project = FpptProjectProvider(pathToRepo, pathToIssueTrackingInfo, pathToRemoteInfo).project
        assertNotNull(project)
    }
}