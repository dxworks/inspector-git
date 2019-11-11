package org.dxworks.inspectorgit.api.configuration.exceptions

class NotConfiguredException(name: String) : Exception("$name is not configured") {
}