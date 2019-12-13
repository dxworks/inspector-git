package org.dxworks.inspectorgit

import org.dxworks.inspectorgit.analyzers.work.WorkAnalyzer
import org.dxworks.inspectorgit.configuration.services.ConfigurationService
import org.dxworks.inspectorgit.persistence.services.ProjectService
import org.dxworks.inspectorgit.transformers.ProjectTransformer
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class AppStartupRunner(private val projectService: ProjectService,
                       private val configurationService: ConfigurationService,
                       private val workAnalyzer: WorkAnalyzer) : CommandLineRunner {
    companion object {
        private val LOG = LoggerFactory.getLogger(AppStartupRunner::class.java)
    }

    private val projectName = "logTest"

    override fun run(vararg args: String) {
//        val projectDTO = projectService.findProjectByName(projectName)
//        configurationService.configureAll()
//        if (workAnalyzer.configured) {
//            val project = ProjectTransformer(projectDTO).transform()
//            val results = workAnalyzer.analyze(project)
//            print("New work: ")
//            println(results.map { it.newWork.size }.toIntArray().sum())
//            print("Legacy refactor: ")
//            println(results.map { it.legacyRefactor.size }.toIntArray().sum())
//            print("Help Others: ")
//            println(results.map { it.helpOthers.size }.toIntArray().sum())
//            print("Churn: ")
//            println(results.map { it.churn.size }.toIntArray().sum())
//        }
    }
}