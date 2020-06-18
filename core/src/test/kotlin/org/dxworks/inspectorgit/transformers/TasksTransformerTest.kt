package org.dxworks.inspectorgit.transformers

import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

internal class TasksTransformerTest {
    @Test
    fun `date should be parsed correctly`() {
        val dateString = "2020-04-07T08:30:48.589+0300"
        val date = TasksTransformer.dateFormatter.parse(dateString)
        println(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date))
        println(TasksTransformer.dateFormatter.format(date))
        println(TasksTransformer.dateFormatter.format(ZonedDateTime.now()))
    }

    @Test
    fun `test task regex`() {
        val message = "Merge pull request #56 from andrei2699/MEA-44\n[MEA-44] - Conexiune cu baza de date"
        val regex = "\\W(MEA-\\d+)\\W".toRegex()
        val all = regex.findAll(message).toList()
        assertEquals(2, all.size)
    }
}