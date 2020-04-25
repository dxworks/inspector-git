package org.dxworks.inspectorgit.fppt.configuration

import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class FpptConfigurer {
    private val configFilePath = "./igconf.properties"
    private val repositoryPathKey = "repository.path"
    private val repositoryPathEnv = "FPPT_IG_REPO_PATH"

    private val taskPrefixesKey = "task.prefixes"
    private val taskPrefixesEnv = "FPPT_IG_TASK_PREFIXES"

    private val tasksFilePathKey = "tasks.path"
    private val tasksFilePathEnv = "FPPT_IG_TASKS_PATH"

    private val devMergesFilePathKey = "dev.merges.path"
    private val devMergesFilePathEnv = "FPPT_IG_DEV_MERGES_PATH"

    private val properties = Properties()

    companion object {
        private var configuration: FpptConfiguration? = null
    }

    fun getConfiguration(): FpptConfiguration {
        val file = Paths.get(configFilePath).toFile()
        if (file.exists())
            properties.load(file.inputStream())

        if (configuration == null)
            configuration = FpptConfiguration(
                    repositoryPath = getRepositoryPath(),
                    taskPrefixes = getTaskPrefixes(),
                    tasksFilePath = getTasksFilePath(),
                    devMergesFilePath = getDevMergesFilePath()
            )
        return configuration!!
    }

    private fun getDevMergesFilePath(): Path? {
        val path = getFromEnv(devMergesFilePathEnv) ?: properties.getProperty(devMergesFilePathKey)
        return path?.let { Paths.get(it) }
    }

    private fun getTasksFilePath(): Path? {
        val path = getFromEnv(tasksFilePathEnv) ?: properties.getProperty(tasksFilePathKey)
        return path?.let { Paths.get(it) }
    }

    private fun getTaskPrefixes(): List<String> {
        val prefixesCsv = getFromEnv(taskPrefixesEnv) ?: properties.getProperty(repositoryPathKey)
        return prefixesCsv?.let { it.split(",").map { it.trim() } } ?: emptyList()
    }

    private fun getRepositoryPath(): Path {
        val path = getFromEnv(repositoryPathEnv) ?: properties.getProperty(repositoryPathKey)
        ?: error("No repository provided")
        return Paths.get(path)
    }

    private fun getFromEnv(key: String) = System.getenv(key)
}