package org.dxworks.inspectorgit.parsers

import net.lingala.zip4j.core.ZipFile
import org.dxworks.inspectorgit.GitClient
import org.dxworks.inspectorgit.resourcesPath
import org.dxworks.inspectorgit.utils.JsonUtils
import org.junit.Test
import java.nio.file.Paths
import kotlin.test.assertEquals

internal class LogParserTest {

    private val targetRepoPath = Paths.get(System.getProperty("java.io.tmpdir"), "inspectorGitTestRepo")

    @Test
    fun parse() {

        val zipFile = ZipFile(resourcesPath.resolve("gitLogTest.zip").toFile())
        zipFile.extractAll(targetRepoPath.toString())

        val gitClient = GitClient(targetRepoPath.resolve("gitLogTest"))
        val projectDTO = LogParser(gitClient).parse(gitClient.getLogs())
        val actual = JsonUtils.toJson(projectDTO)
        val expected = resourcesPath.resolve("gitLogTest.json").toFile().readLines().first()
        targetRepoPath.toFile().delete()
        assertEquals(expected, actual)
    }
}