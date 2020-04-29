package org.dxworks.inspectorgit.compassmetrics

import java.time.LocalDate
import java.time.ZonedDateTime

class Period(private val start: LocalDate, private val end: LocalDate) {
    fun contains(zonedDateTime: ZonedDateTime): Boolean {
        return start < zonedDateTime.toLocalDate() && zonedDateTime.toLocalDate() < end
    }
}