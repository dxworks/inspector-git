package org.dxworks.inspectorgit.gitClient

import org.dxworks.inspectorgit.utils.OsUtils
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.nio.file.Path

class GitClient(path: Path) {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitClient::class.java)
    }

    private val git = "git"
    private val renameDetectionThreshold = "-M25%"
    private val contextThreshold = "-U0"

    private val gitLogCommand = "$git log $renameDetectionThreshold -c $contextThreshold --encoding=UTF-8 --format=\"commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%nauthor date: %ad%ncommitter name: %cn%ncommitter email: %ce%ncommitter date: %cd %nmessage:%n%s%n%b\""
    private val gitAffectedFilesCommand = "$git log $renameDetectionThreshold -m -1 --name-only --pretty=\"format:\""
    private val gitDiffCommand = "$git diff $renameDetectionThreshold $contextThreshold"
    private val gitBlameCommand = "$git blame -l"
    private val gitBranchCommand = "$git branch"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    val branch: String? = runCommand(gitBranchCommand).find { it.startsWith("* ") }?.removePrefix("* ")

    fun getLogs(): List<String> = runCommand(gitLogCommand)

    fun diff(parent: String, revision: String, file: String): List<String> = runCommand("$gitDiffCommand $parent $revision -- $file")

    fun blame(revision: String, file: String): List<String> = runCommand("$gitBlameCommand $file $revision")

    fun affectedFiles(revision: String): List<String> = runCommand("$gitAffectedFilesCommand $revision")

    private fun runCommand(command: String): List<String> {
        LOG.info("Running command: $command")

        processBuilder.command(OsUtils.commandInterpreterPrefix, OsUtils.interpreterArg, command)
        val process = processBuilder.start()
        val lines = getLines(process.inputStream)

        return if (process.waitFor() == 0) {
            LOG.info("Command completed")
            lines
        } else {
            LOG.error("Command completed with errors")
            emptyList()
        }
    }

    private fun getLines(inputStream: InputStream): List<String> {
        val reader = BufferedReader(inputStream.reader())
        val lines: MutableList<String> = ArrayList()
        reader.forEachLine { lines.add(it) }
        return lines
    }
}