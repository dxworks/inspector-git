package org.dxworks.inspectorgit.services

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import groovy.lang.Binding
import groovy.lang.Closure
import groovy.lang.GroovyShell
import org.dxworks.inspectorgit.AccountMerge
import org.dxworks.inspectorgit.services.chronos.chart.BarChartDTO
import org.dxworks.inspectorgit.services.chronos.chart.convertBarChartToRequestBody
import org.dxworks.inspectorgit.services.chronos.chart.exportChart
import org.dxworks.inspectorgit.services.dto.GroovyScriptDTO
import org.dxworks.inspectorgit.services.dto.LocalSystemDTO
import org.dxworks.inspectorgit.services.dto.ScriptResult
import org.dxworks.inspectorgit.utils.FileSystemUtils.Companion.getScriptResultsPathForSystem
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.PrintWriter
import java.io.StringWriter

@Service
class AnalysisService(private val loadedSystem: LoadedSystem,
                      private val accountMergeService: AccountMergeService,
                      private val messageClassifierService: MessageClassifierService) {
    companion object {
        private val LOG = LoggerFactory.getLogger(AnalysisService::class.java)
    }

    fun runGroovyScript(groovyScriptDTO: GroovyScriptDTO): ScriptResult {
        val stringWriter = StringWriter()

        val outputFiles: MutableList<String> = ArrayList()

        try {
            if (!loadedSystem.isSet) {
                throw IllegalStateException("There is no loaded system")
            }

            val binding = getBindings(stringWriter, outputFiles)

            val shell = GroovyShell(binding)
            shell.evaluate(prepareScript(groovyScriptDTO.script))

        } catch (e: Exception) {
            e.printStackTrace(PrintWriter(stringWriter))
            LOG.error("Exception executing groovy script", e)
        }
        return ScriptResult(stringWriter.toString(), outputFiles)
    }

    private fun prepareScript(script: String) =
            script.lines().filterNot { it.matches(Regex(".*import\\s+static\\s+org\\.dxworks\\.inspectorgit\\.services\\.ScriptUtilsKt.*")) }.joinToString(separator = "\n")

    private fun getBindings(stringWriter: StringWriter, outputFiles: MutableList<String>): Binding {
        val binding = Binding()
        val jsonMapper = jacksonObjectMapper()
        val csvMapper = CsvMapper().registerModule(KotlinModule())

        binding.setVariable("log", stringWriter)
        binding.setVariable("system", loadedSystem)
        binding.setVariable("messageClassifier", messageClassifierService)
        binding.setVariable("exportJson", object : Closure<Unit>(null) {

            override fun call(vararg args: Any?) {
                val folderPath = getScriptResultsPathForSystem(loadedSystem.id)
                folderPath.toFile().mkdirs()
                val file = folderPath.resolve("${args[1].toString()}.json").toFile()
                jsonMapper.writerWithDefaultPrettyPrinter().writeValue(file, args[0])
                outputFiles.add(file.name)
            }
        })

        binding.setVariable("exportCsv", object : Closure<Unit>(null) {

            override fun call(vararg args: Any?) {
                if (args[0] is List<*>) {
                    if ((args[0] as List<*>).size > 0) {
                        val list = args[0] as List<Map<String, Any>>

                        val builder = CsvSchema.builder()
                        list[0].keys.forEach { builder.addColumn(it) }
                        val csvSchema = builder.build().withHeader()
                        val folderPath = getScriptResultsPathForSystem(loadedSystem.id)
                        folderPath.toFile().mkdirs()
                        val file = folderPath.resolve("${args[1].toString()}.csv").toFile()
                        csvMapper.writer(csvSchema).writeValue(file, args[0])
                        outputFiles.add(file.name)
                    }
                }
            }
        })

        binding.setVariable("mergeAccounts", object : Closure<Unit>(null) {
            override fun call(vararg args: Any?) {
                val name = args[0].toString()
                accountMergeService.mergeAccounts(AccountMerge(name, args.drop(1).mapNotNull { it?.toString() }))
            }
        })

        binding.setVariable("mergeDevelopers", object : Closure<Unit>(null) {
            override fun call(vararg args: Any?) {
                val name = args[0].toString()
                accountMergeService.mergeDevelopers(AccountMerge(name, args.drop(1).mapNotNull { it?.toString() }))
            }
        })

        binding.setVariable("exportBarChart", object : Closure<Unit>(null) {

            override fun call(vararg args: Any?) {
                if (args[0] is BarChartDTO) {
                    val barChartDTO: BarChartDTO = args[0] as BarChartDTO
                    val requestBody = convertBarChartToRequestBody(barChartDTO)

                    if (args.size == 3 && args[1] is String && args[2] is String)
                        exportChart(requestBody, args[1] as String, args[2] as String)
                    else
                        exportChart(requestBody, args[1] as String)
                }
            }
        })

        return binding
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
