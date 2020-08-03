package org.dxworks.inspectorgit.services.impl

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.lang.Binding
import groovy.lang.Closure
import groovy.lang.GroovyShell
import org.dxworks.inspectorgit.dto.GroovyScriptDTO
import org.dxworks.inspectorgit.dto.ScriptResult
import org.dxworks.inspectorgit.dto.localProjects.LocalSystemDTO
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getScriptResultsPathForSystem
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.PrintWriter
import java.io.StringWriter

@Service
class AnalysisService(private val loadedSystem: LoadedSystem) {
    companion object {
        private val LOG = LoggerFactory.getLogger(AnalysisService::class.java)
    }

    fun runGroovyScript(groovyScriptDTO: GroovyScriptDTO): ScriptResult {
        val stringWriter = StringWriter()

        val objectMapper = ObjectMapper()

        val outputFiles: MutableList<String> = ArrayList()

        try {
            if (!loadedSystem.isSet) {
                throw IllegalStateException("There is no loaded system")
            }

            val binding = Binding()

            binding.setVariable("log", stringWriter)
            binding.setVariable("system", loadedSystem)
            binding.setVariable("export", object : Closure<Unit>(null) {

                override fun call(vararg args: Any?) {
                    val folderPath = getScriptResultsPathForSystem(loadedSystem.id)
                    folderPath.toFile().mkdirs()
                    val file = folderPath.resolve("${args[1].toString()}.json").toFile()
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, args[0])
                    outputFiles.add(file.name)
                }
            })

            val shell = GroovyShell(binding)
            shell.evaluate(groovyScriptDTO.script)

        } catch (e: Exception) {
            e.printStackTrace(PrintWriter(stringWriter))
            LOG.error("Exception executing groovy script", e)
        }
        return ScriptResult(stringWriter.toString(), outputFiles)
    }

    fun getDetails(): LocalSystemDTO? =
            if (loadedSystem.isSet)
                LocalSystemDTO(
                        loadedSystem.id,
                        loadedSystem.name,
                        loadedSystem.gitProjects.keys.toList(),
                        loadedSystem.issueProjects.keys.toList(),
                        loadedSystem.remoteProjects.keys.toList()
                )
            else
                null

}