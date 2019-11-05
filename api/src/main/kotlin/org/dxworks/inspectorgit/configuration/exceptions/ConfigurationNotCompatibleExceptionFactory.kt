package org.dxworks.inspectorgit.configuration.exceptions

class ConfigurationExceptionFactory {
    companion object {
        fun missingField(name: String, field: String) =
                ConfigurationNotCompatibleException("Configuration for $name is missing value for field: $field")

        fun fieldDoesntMatch(name: String?, key: String, pattern: String) =
                ConfigurationNotCompatibleException("In configuration for $name: $key doesnt match pattern $pattern")
    }
}