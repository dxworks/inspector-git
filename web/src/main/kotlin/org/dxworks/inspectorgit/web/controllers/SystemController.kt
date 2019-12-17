package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.dto.SystemDTO
import org.dxworks.inspectorgit.services.SystemService
import org.dxworks.inspectorgit.web.apiPath
import org.dxworks.inspectorgit.web.services.GitlabIntegrationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$apiPath/system")
class SystemController(private val systemService: SystemService,
                       private val gitlabIntegrationService: GitlabIntegrationService) {
    @PostMapping("create")
    fun create(@RequestBody systemDTO: SystemDTO) {
        systemDTO.projects?.filter { it.platform == "gitlab" }?.let { gitlabIntegrationService.import(it) }
        systemService.create(systemDTO)
    }
}