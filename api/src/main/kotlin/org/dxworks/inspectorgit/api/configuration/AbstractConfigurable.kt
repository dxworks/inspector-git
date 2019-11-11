package org.dxworks.inspectorgit.api.configuration

import java.util.*

abstract class AbstractConfigurable<T : Configuration> : Configurable {
    protected lateinit var configuration: T
    protected open val defaultProperties: Properties = Properties()

    protected abstract fun setConfiguration(properties: Properties): T

    override val configured: Boolean
        get() = this::configuration.isInitialized

    override fun configure(properties: Properties) {
        defaultProperties.forEach { key, value -> properties.putIfAbsent(key, value) }
        configuration = setConfiguration(properties)
    }
}