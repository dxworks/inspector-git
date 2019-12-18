package org.dxworks.inspectorgit.services

import org.dxworks.inspectorgit.analyzers.work.WorkAnalyzerNumbersDTO
import org.dxworks.inspectorgit.analyzers.work.WorkAnalyzerResult
import org.dxworks.inspectorgit.analyzers.work.WorkAnalyzerResultDTO
import org.dxworks.inspectorgit.dto.SystemDTO

interface SystemService {
    fun create(systemDTO: SystemDTO)
    fun findAll(): List<SystemDTO>
    fun delete(systemId: String)
    fun analyze(systemId: String): Map<String, List<WorkAnalyzerNumbersDTO>>?
}