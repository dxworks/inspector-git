package org.dxworks.inspectorgit.api.configuration.validators

import org.dxworks.inspectorgit.api.configuration.Validator
import org.dxworks.inspectorgit.api.configuration.exceptions.ValidationException
import java.util.*

class RegexValidator(private val fields: List<String>, private val regex: Regex) : Validator {
    override fun validate(properties: Properties) {
        fields.forEach {
            try {
                if (!properties.getProperty(it).matches(regex))
                    throw ValidationException("In ${this.javaClass.simpleName}: $it did not match ${regex.pattern}")
            } catch (e: IllegalStateException) {
                throw throw ValidationException("Missing field $it")
            }
        }
    }
}