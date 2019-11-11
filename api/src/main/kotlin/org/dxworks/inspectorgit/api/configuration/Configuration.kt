package org.dxworks.inspectorgit.api.configuration

import org.dxworks.inspectorgit.api.configuration.exceptions.ValidationException
import org.slf4j.LoggerFactory
import java.util.*

interface Configuration {
    companion object {
        private val LOG = LoggerFactory.getLogger("Configuration")
    }

    fun validate(properties: Properties, validators: List<Validator>) {
        validators.forEach {
            try {
                it.validate(properties)
            } catch (e: ValidationException) {
                LOG.error(e.message)
            }

        }
    }
}