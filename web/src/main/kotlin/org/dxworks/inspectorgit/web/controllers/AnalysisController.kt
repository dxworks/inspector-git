package org.dxworks.inspectorgit.web.controllers

import org.dxworks.inspectorgit.dto.GroovyScriptDTO
import org.dxworks.inspectorgit.dto.ScriptResult
import org.dxworks.inspectorgit.dto.localProjects.LocalSystemDTO
import org.dxworks.inspectorgit.services.impl.AnalysisService
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getScriptResult
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.openFile
import org.dxworks.inspectorgit.web.apiPath
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$apiPath/analysis")
class AnalysisController(private val analysisService: AnalysisService) {
    @PostMapping("groovy")
    fun runGroovyScript(@RequestBody groovyScriptDTO: GroovyScriptDTO): ScriptResult {
        return analysisService.runGroovyScript(groovyScriptDTO)
    }

    @GetMapping("systemDetails")
    fun getDetails(): LocalSystemDTO? {
        return analysisService.getDetails()
    }

    @PostMapping("results")
    fun getResult(@RequestBody body: ResultFileNameDTO): ResultFileDTO {
        return ResultFileDTO(body.fileName, getScriptResult(body.systemId, body.fileName))
    }

    @PostMapping("results/open")
    fun openResult(@RequestBody body: ResultFileNameDTO): ResponseEntity<Unit> {
        openFile(body.systemId, body.fileName)
        return ResponseEntity.ok().build()
    }

    data class ResultFileNameDTO(val fileName: String, val systemId: String)
    data class ResultFileDTO(val fileName: String, val content: String)
}