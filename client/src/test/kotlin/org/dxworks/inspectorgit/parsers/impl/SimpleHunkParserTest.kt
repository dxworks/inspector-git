package org.dxworks.inspectorgit.parsers.impl

import org.dxworks.inspectorgit.dto.LineChangeDTO
import org.dxworks.inspectorgit.enums.LineOperation
import org.dxworks.inspectorgit.resourcesPath
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SimpleHunkParserTest {

    private val hunkTest1File = "hunkTest1.log"

    @Test
    fun extractLineChanges() {
        val lines: List<String> = resourcesPath.resolve(hunkTest1File).toFile().readLines()
        val actual = SimpleHunkParser().parse(lines)
        val lineChanges = actual.lineChanges
        assertThatLineChangesAreWhatTheyShouldBe(lineChanges)
    }

    private fun assertThatLineChangesAreWhatTheyShouldBe(lineChanges: List<LineChangeDTO>) {
        assertEquals(2, lineChanges.size)
        assertEquals(LineOperation.REMOVE, lineChanges[0].operation)
        assertEquals(LineOperation.REMOVE, lineChanges[1].operation)
        assertEquals(1, lineChanges[0].number)
        assertEquals(2, lineChanges[1].number)
        assertEquals("*.iml", lineChanges[0].content)
        assertEquals(".idea/", lineChanges[1].content)
    }
}