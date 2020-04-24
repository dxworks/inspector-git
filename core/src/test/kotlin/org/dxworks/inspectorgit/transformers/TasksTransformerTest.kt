package org.dxworks.inspectorgit.transformers

import org.junit.jupiter.api.Test
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

internal class TasksTransformerTest {
    @Test
    fun `date should be parsed correctly`() {
        val dateString = "2020-04-07T08:30:48.589+0300"
        val date = TasksTransformer.dateFormatter.parse(dateString)
        println(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(date))
        println(TasksTransformer.dateFormatter.format(date))
        println(TasksTransformer.dateFormatter.format(ZonedDateTime.now()))
    }
}