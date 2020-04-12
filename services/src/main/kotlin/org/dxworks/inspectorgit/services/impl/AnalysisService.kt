package org.dxworks.inspectorgit.services.impl

import groovy.lang.Binding
import groovy.lang.GroovyShell
import org.dxworks.inspectorgit.dto.GroovyScriptDTO
import org.springframework.stereotype.Service
import java.io.StringWriter

@Service
class AnalysisService(private val loadedSystem: LoadedSystem) {
    fun runGroovyScript(groovyScriptDTO: GroovyScriptDTO): String {
        if (!loadedSystem.isSet) {
            throw IllegalStateException("There is no loaded system")
        }

        val binding = Binding()
        val stringWriter = StringWriter()

        binding.setVariable("log", stringWriter)
        binding.setVariable("system", loadedSystem)

        val shell = GroovyShell(binding)
        shell.evaluate(groovyScriptDTO.script)

        return stringWriter.toString()
    }
}