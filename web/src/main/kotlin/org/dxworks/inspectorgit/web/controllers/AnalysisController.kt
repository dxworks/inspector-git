package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.dto.GroovyScriptDTO
import org.dxworks.inspectorgit.services.impl.AnalysisService
import org.dxworks.inspectorgit.web.apiPath
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$apiPath/analysis")
class AnalysisController(private val analysisService: AnalysisService) {
    @PostMapping("groovy")
    fun runGroovyScript(@RequestBody groovyScriptDTO: GroovyScriptDTO): ResponseEntity<String> {
        return ResponseEntity.ok(analysisService.runGroovyScript(groovyScriptDTO))
    }
}