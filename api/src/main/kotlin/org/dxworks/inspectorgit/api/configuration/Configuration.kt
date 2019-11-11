package org.dxworks.inspectorgit.api.configuration

import java.util.*

interface Configuration {
    fun validate(properties: Properties, validators: List<Validator>) {
        validators.forEach { it.validate(properties) }
    }
}