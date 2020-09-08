package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.services.LocalSystemsService
import org.dxworks.inspectorgit.services.dto.LocalSystemDTO
import org.dxworks.inspectorgit.web.apiPath
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$apiPath/localSystems")
class LocalSystemsController(private val localSystemsService: LocalSystemsService) {
    @PostMapping
    fun createSystem(@RequestBody localSystemDTO: LocalSystemDTO) {
        localSystemsService.create(localSystemDTO)
    }

    @GetMapping
    fun listSystems(): List<LocalSystemDTO>{
        return localSystemsService.list()
    }

    @GetMapping("/{id}")
    fun loadSystem(@PathVariable("id") id: String) {
        localSystemsService.load(id)
    }

    @DeleteMapping("/{id}")
    fun deleteSystem(@PathVariable("id") id: String) {
        localSystemsService.delete(id)
    }
}