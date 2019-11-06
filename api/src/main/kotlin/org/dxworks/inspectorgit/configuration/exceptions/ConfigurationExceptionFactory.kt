package org.dxworks.inspectorgit.configuration.exceptions

class ConfigurationExceptionFactory {
    companion object {
        fun missingField(name: String, field: String) =
                ConfigurationNotCompatibleException("$name is missing value for $field")

        fun fieldDoesntMatch(name: String?, key: String, pattern: String) =
                ConfigurationNotCompatibleException("In $name: $key doesnt match pattern $pattern")
    }
}