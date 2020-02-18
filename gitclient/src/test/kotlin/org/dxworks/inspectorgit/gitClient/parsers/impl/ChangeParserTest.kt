package org.dxworks.inspectorgit.gitClient.parsers.impl

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ChangeParserTest {

    private val changeParser = ChangeParser("")

    @Test
    fun `extract simple file name`() {
        val diffLine = "diff --git a/core/src/main/scala/kafka/admin/TopicCommand.scala b/core/src/main/scala/kafka/admin/TopicCommand.scala"
        val privateExtractFileNameMethod = ChangeParser::class.java.getDeclaredMethod("extractFileName", String::class.java)
        privateExtractFileNameMethod.trySetAccessible()
        val extractedFileName = privateExtractFileNameMethod.invoke(changeParser, diffLine).toString()
        assertEquals("core/src/main/scala/kafka/admin/TopicCommand.scala", extractedFileName)
    }

    @Test
    fun `extract file name containing to name prefix`() {
        val diffLine = "diff --git a/core/src/main b/scala/kafka b/admin/TopicCommand.scala b/core/src/main b/scala/kafka b/admin/TopicCommand.scala"
        val privateExtractFileNameMethod = ChangeParser::class.java.getDeclaredMethod("extractFileName", String::class.java)
        privateExtractFileNameMethod.trySetAccessible()
        val extractedFileName = privateExtractFileNameMethod.invoke(changeParser, diffLine).toString()
        assertEquals("core/src/main b/scala/kafka b/admin/TopicCommand.scala", extractedFileName)
    }
}