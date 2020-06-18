package org.dxworks.inspectorgit.fppt.configuration

import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class FpptConfigurer {
    private val configFilePath = "./igconf.properties"
    private val repositoryPathKey = "repository"
    private val repositoryPathEnv = "FPPT_IG_REPO_PATH"

    private val iglogPathKey = "iglog"
    private val iglogPathEnv = "FPPT_IG_LOG_PATH"

    private val remoteInfoPathKey = "remote.info"
    private val remoteInfoPathEnv = "FPPT_IG_REMOTE_INFO_PATH"

    private val taskPrefixesKey = "task.prefixes"
    private val taskPrefixesEnv = "FPPT_IG_TASK_PREFIXES"

    private val tasksFilePathKey = "tasks"
    private val tasksFilePathEnv = "FPPT_IG_TASKS_PATH"

    private val devMergesFilePathKey = "dev.merges"
    private val devMergesFilePathEnv = "FPPT_IG_DEV_MERGES_PATH"

    private val properties = Properties()

    companion object {
        private var configuration: FpptConfiguration? = null
    }

    fun getConfiguration(): FpptConfiguration {
        val file = Paths.get(configFilePath).toFile()
        if (file.exists())
            properties.load(file.inputStream())

        if (configuration == null) {
            getPath(repositoryPathEnv, repositoryPathKey)
            configuration = FpptConfiguration(
                    repositoryPath = getPath(repositoryPathEnv, repositoryPathKey),
                    iglogPath = getPath(iglogPathEnv, iglogPathKey),
                    taskPrefixes = getTaskPrefixes(),
                    tasksFilePath = getPath(tasksFilePathEnv, tasksFilePathKey),
                    devMergesFilePath = getPath(devMergesFilePathEnv, devMergesFilePathKey),
                    remoteInfoPath = getPath(remoteInfoPathEnv, remoteInfoPathKey)
            )
        }
        return configuration!!
    }

    private fun getTaskPrefixes(): List<String> {
        val prefixesCsv = getFromEnv(taskPrefixesEnv) ?: properties.getProperty(taskPrefixesKey)
        return prefixesCsv?.let { it.split(",").map { it.trim() } } ?: emptyList()
    }

    private fun getPath(envVarName: String, propertyKey: String): Path? {
        val path = getFromEnv(envVarName) ?: properties.getProperty(propertyKey)
        return path?.let { Paths.get(it) }
    }

    private fun getFromEnv(key: String) = System.getenv(key)
}
