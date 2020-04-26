package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.services.IntegrationService
import org.dxworks.inspectorgit.services.dto.IntegrationDTO
import org.dxworks.inspectorgit.web.apiPath
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$apiPath/integration")
class IntegrationController(private val integrationService: IntegrationService) {
    @GetMapping
    fun getIntegrations() = integrationService.getAll()

    @GetMapping("{platform}")
    fun getByPlatform(@PathVariable platform: String) = integrationService.getByPlatform(platform)


    @PostMapping("create")
    fun createIntegration(@RequestBody integrationDTO: IntegrationDTO) {
        integrationService.create(integrationDTO)
    }

    @DeleteMapping("deleteIntegration")
    fun deleteIntegration(@RequestParam(required = true) name: String, @RequestParam(required = true) platform: String) {
        integrationService.deleteByNameAndPlatform(name, platform)
    }
}