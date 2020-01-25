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

    private val gitLogCommand = "log $renameDetectionThreshold -c $contextThreshold --encoding=UTF-8 --format=\"commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%nauthor date: %ad%ncommitter name: %cn%ncommitter email: %ce%ncommitter date: %cd %nmessage:%n%s%n%b\""
    private val gitAffectedFilesCommand = "log $renameDetectionThreshold -m -1 --name-only --pretty=\"format:\""
    private val gitDiffCommand = "diff $renameDetectionThreshold $contextThreshold"
    private val gitBlameCommand = "blame -l"
    private val gitBranchCommand = "branch"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    val branch: String? = runGitCommand(gitBranchCommand).find { it.startsWith("* ") }?.removePrefix("* ")

    fun getLogs(): List<String> = runGitCommand(gitLogCommand)

    fun diff(parent: String, revision: String, file: String): List<String> = runGitCommand("$gitDiffCommand $parent $revision -- $file")

    fun blame(revision: String, file: String): List<String> = runGitCommand("$gitBlameCommand $file $revision")

    fun affectedFiles(revision: String): List<String> = runGitCommand("$gitAffectedFilesCommand $revision")

    fun runGitCommand(args: String): List<String> {
        val command = "$git $args"
        LOG.info("Running command: $command")

        processBuilder.command(OsUtils.commandInterpreterPrefix, OsUtils.interpreterArg, command)
        val process = processBuilder.start()
        val lines = getLines(process.inputStream)

        return if (process.waitFor() == 0) {
            LOG.info("Command completed")
            lines
        } else {
            LOG.error("Command completed with errors:\n ${getLines(process.errorStream).joinToString("\n")}")
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