package org.dxworks.inspectorgit.parsers

import org.dxworks.inspectorgit.GitClient
import org.dxworks.inspectorgit.utils.JsonUtils
import org.junit.jupiter.api.Test
import java.nio.file.Paths
import kotlin.test.assertEquals

internal class LogParserTest {

    private val resourcesPath = Paths.get("src", "test", "resources")

    @Test
    fun parse() {
        val gitClient = GitClient(resourcesPath.resolve("gitLogTest"))
        val projectDTO = LogParser(gitClient).parse(gitClient.getLogs())
        val actual = JsonUtils.toJson(projectDTO)
        val expected = resourcesPath.resolve("gitLogTest.json").toFile().readLines().first()
        assertEquals(expected, actual)
    }
}