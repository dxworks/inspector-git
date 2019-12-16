package org.dxworks.inspectorgit.dto

import org.dxworks.inspectorgit.persistence.entities.IntegrationEntity

data class IntegrationDTO(
        val name: String,
        val username: String,
        val platform: String,
        val url: String,
        val password: String?) {
    fun toEntity() = IntegrationEntity(name, platform, username, url, password!!)

    companion object {
        fun fromEntity(entity: IntegrationEntity) = IntegrationDTO(entity.name, entity.username, entity.platform, entity.url, entity.password)
    }
}