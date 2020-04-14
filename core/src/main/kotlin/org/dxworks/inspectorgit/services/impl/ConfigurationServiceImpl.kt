package org.dxworks.inspectorgit.services.impl

import org.dxworks.inspectorgit.api.configuration.Configurable
import org.dxworks.inspectorgit.services.ConfigurationService
import org.dxworks.inspectorgit.utils.propertyFilePath
import org.slf4j.LoggerFactory
import java.util.*

class ConfigurationServiceImpl(private val configurables: List<Configurable>) : ConfigurationService {
    companion object {
        private val LOG = LoggerFactory.getLogger(ConfigurationServiceImpl::class.java)
    }

    override fun configureAll() {
        val properties = loadProperties()
        configurables.forEach {
            try {
                it.configure(properties)
            } catch (e: Exception) {
                LOG.error("${it.javaClass.simpleName} could not be configured", e)
            }
        }
    }

    private fun loadProperties(): Properties {
        val properties = Properties()
        try {
            properties.load(propertyFilePath.toFile().inputStream())
        } catch (e: Exception) {
            LOG.error("Could not load properties file", e)
        }
        return properties
    }
}