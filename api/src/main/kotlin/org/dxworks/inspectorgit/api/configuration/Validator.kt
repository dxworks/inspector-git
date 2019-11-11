package org.dxworks.inspectorgit.api.configuration

import java.util.*

interface Validator {
    fun validate(properties: Properties)
}