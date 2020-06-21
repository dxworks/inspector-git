package org.dxworks.inspectorgit.transformers

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

internal class RemoteInfoTransformerTest {
    @Test
    fun `parse date to zoned date time`() {
        val date = "2020-06-08T09:23:01Z"
        val localDateTime = LocalDateTime.parse(date, RemoteInfoTransformer.dateFormatter)
        println(localDateTime.atZone(ZoneId.of("Z")))
    }
}