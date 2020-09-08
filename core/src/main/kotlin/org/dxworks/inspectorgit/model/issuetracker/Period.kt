package org.dxworks.inspectorgit.model.issuetracker

import java.time.ZonedDateTime

class Period(val start: ZonedDateTime, val end: ZonedDateTime) {
    fun contains(zonedDateTime: ZonedDateTime): Boolean {
        return start < zonedDateTime && zonedDateTime < end
    }
}