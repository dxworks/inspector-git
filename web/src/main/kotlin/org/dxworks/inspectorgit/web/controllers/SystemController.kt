package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.dto.SystemDTO
import org.dxworks.inspectorgit.services.SystemService
import org.dxworks.inspectorgit.web.apiPath
import org.dxworks.inspectorgit.web.dto.CreateSystemDTO
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
    fun create(@RequestBody createSystemDTO: CreateSystemDTO) {
        gitlabIntegrationService.import(createSystemDTO.gitlabIntegrationProjectsDTO)
        systemService.create(SystemDTO(createSystemDTO.name, createSystemDTO.gitlabIntegrationProjectsDTO.projects))
    }
}