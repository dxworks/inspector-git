package org.dxworks.inspectorgit.fppt

import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.test.assertNotNull

internal class FpptProjectProviderTest {

    @Test
    fun getProject() {
//        val base = "C:\\Users\\dnagy\\Documents\\personal\\fppt\\project"
//        val pathToRepo = Paths.get("$base\\Music-Events-Application")
//        val pathToIssueTrackingInfo = Paths.get("$base\\mea-tasks.json")
//        val pathToRemoteInfo = Paths.get("$base\\remote-export.json")
        val pathToRepo = Paths.get("C:\\Users\\dnagy\\.fppt\\students\\Alexandra Glentoaica,Ionut Grigore,\\.\\GeekShop-FIS2020")
        val pathToIssueTrackingInfo = Paths.get("C:\\Users\\dnagy\\.fppt\\students\\Alexandra Glentoaica,Ionut Grigore,\\.\\cache\\jira.json")
        val pathToRemoteInfo = Paths.get("C:\\Users\\dnagy\\.fppt\\students\\Alexandra Glentoaica,Ionut Grigore,\\.\\cache\\github.json")
        val project = FpptProjectProvider(pathToRepo, pathToIssueTrackingInfo, pathToRemoteInfo).project
        assertNotNull(project)
    }
}