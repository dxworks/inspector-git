package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.cli.Options
import org.dxworks.inspectorgit.cli.usageMessage
import org.dxworks.inspectorgit.services.AnalysisService
import org.dxworks.inspectorgit.services.LocalSystemsService
import org.dxworks.inspectorgit.services.chronos.ChronosSettingsService
import org.dxworks.inspectorgit.services.dto.GroovyScriptDTO
import org.dxworks.inspectorgit.services.dto.LocalSystemDTO
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.io.File
import java.nio.file.Paths
import kotlin.system.exitProcess

@EntityScan("org.dxworks.inspectorgit.persistence")
@EnableJpaRepositories(basePackages = ["org.dxworks.inspectorgit.persistence"])
@Profile("cli")
@SpringBootApplication
class InspectorgitCli(
        private val localSystemsService: LocalSystemsService,
        private val analysisService: AnalysisService,
        private val chronosSettingsService: ChronosSettingsService
) : ApplicationRunner {
    companion object {
        private val LOG = LoggerFactory.getLogger(InspectorgitCli::class.java)
    }

    override fun run(args: ApplicationArguments?) {
        if (args == null) {
            usage()
            return
        }

        val id = args.getOptionValues(Options.id)[0]
        val computeAnnotatedLines = args.getOptionValues(Options.computeAnnotatedLines)[0]?.toBoolean() ?: true
        try {
            localSystemsService.load(id, computeAnnotatedLines)
        } catch (_: Exception) {
        }
        try {
            localSystemsService.create(LocalSystemDTO(
                    id,
                    id,
                    getSourceFiles(args.getOptionValues(Options.iglogs)),
                    getFiles(args.getOptionValues(Options.issues)).map { it.absolutePath },
                    getFiles(args.getOptionValues(Options.remotes)).map { it.absolutePath }
            ))

            if (args.containsOption(Options.chronosSettings))
                chronosSettingsService.applyMerges(Paths.get(args.getOptionValues(Options.chronosSettings)[0]).toFile())

            getFiles(args.getOptionValues(Options.scripts))
                    .forEach { analysisService.runGroovyScript(GroovyScriptDTO(it.readText())) }
        } catch (e: Exception) {
            e.printStackTrace()
            usage()
        }
        exitProcess(0)
    }

    private fun getFiles(paths: List<String>): List<File> {
        return paths.map {
            Paths.get(it).toFile()
        }.flatMap {
            if (it.isDirectory) {
                it.listFiles()?.toList() ?: emptyList()
            } else listOf(it)
        }
    }

    private fun getSourceFiles(paths: List<String>): List<String> {
        return paths.map {
            Paths.get(it).toFile()
        }.flatMap {
            if (it.isDirectory && !isGitDir(it)) {
                it.listFiles()?.toList() ?: emptyList()
            } else listOf(it)
        }.map { it.absolutePath }
    }

    private fun isGitDir(file: File): Boolean {
        return file.listFiles()?.any { it.name == ".git" } ?: false
    }

    private fun usage() {
        println(usageMessage)
        exitProcess(0)
    }
}

fun main(args: Array<String>) {
    runApplication<InspectorgitCli>(*args)
}
