package org.dxworks.inspectorgit.api.configuration

import java.util.*

interface Configurable {
    val configured: Boolean
    fun configure(properties: Properties)
}