package org.dxworks.inspectorgit.configuration

import org.dxworks.inspectorgit.configuration.exceptions.ConfigurationExceptionFactory

abstract class Configuration {
    abstract val validation: Map<String, Regex?>

    fun validate(configuration: Map<String, String>) {
        validation.forEach { (key, regex) ->
            val field = configuration[key]
            if (field.isNullOrBlank())
                throw ConfigurationExceptionFactory.missingField(this.javaClass.simpleName, key)
            else if (regex != null && !(field).matches(regex))
                throw ConfigurationExceptionFactory.fieldDoesntMatch(this.javaClass.simpleName, key, regex.pattern)
        }
    }

}