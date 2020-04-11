package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.dto.localProjects.LocalProjectDTO
import org.dxworks.inspectorgit.services.impl.LocalProjectsService
import org.dxworks.inspectorgit.web.apiPath
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$apiPath/localProjects")
class LocalProjectsController(private val localProjectsService: LocalProjectsService) {
    @PostMapping
    fun createProject(@RequestBody localProjectDTO: LocalProjectDTO) {
        localProjectsService.create(localProjectDTO)
    }
}