package org.dxworks.inspectorgit.gitclient

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
        private val renameDetectionThreshold = "-M60%"
//    private val renameDetectionThreshold = "--no-renames"
    private val contextThreshold = "-U1"

    private val gitLogCommand = "log $renameDetectionThreshold -m $contextThreshold --encoding=UTF-8 --format=\"commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%nauthor date: %ad%ncommitter name: %cn%ncommitter email: %ce%ncommitter date: %cd %nmessage:%n%s%n%b\" --reverse"
    private val gitAffectedFilesCommand = "log $renameDetectionThreshold -m -1 --name-only --pretty=\"format:\""
    private val gitCommitLinksCommand = "log -m  --encoding=UTF-8 --format=\"commit: %H%nparents: %P\" --reverse"
    private val gitCountCommitsCommand = "rev-list HEAD --count"
    private val gitDiffCommand = "diff $renameDetectionThreshold $contextThreshold"
    private val gitDiffFileNamesCommand = "diff $renameDetectionThreshold --name-only"
    private val gitBlameCommand = "blame -l"
    private val gitBranchCommand = "branch"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    val branch: String? = runGitCommand(gitBranchCommand)!!.find { it.startsWith("* ") }?.removePrefix("* ")

    fun getLogs(): List<String> = runGitCommand(gitLogCommand)!!


    fun getCommitCount(): Int = runGitCommand(gitCountCommitsCommand)!!.getOrElse(0) { "0" }.toInt()

    fun getCommitLinks(): List<String> = runGitCommand(gitCommitLinksCommand)!!

    fun getNCommitLogs(n: Int, skip: Int = 0): List<String> = runGitCommand("$gitLogCommand --max-count=$n --skip=$skip")!!

    fun diff(parent: String, revision: String, file: String): List<String> = runGitCommand("$gitDiffCommand $parent $revision -- $file")!!

    fun diffFileNames(parent: String, revision: String): List<String> = runGitCommand("$gitDiffFileNamesCommand $revision..$parent")
            ?: emptyList()

    fun blame(revision: String, file: String): List<String>? = runGitCommand("$gitBlameCommand $file $revision")

    fun affectedFiles(revision: String): List<String> = runGitCommand("$gitAffectedFilesCommand $revision")!!

    fun runGitCommand(args: String): List<String>? {
        val command = "$git $args"
        LOG.info("Running command: $command")

        processBuilder.command(OsUtils.commandInterpreterName, OsUtils.interpreterArg, command)
        val process = processBuilder.start()
        val lines = getLines(process.inputStream)

        return if (process.waitFor() == 0) {
            LOG.info("Command completed")
            lines
        } else {
            LOG.error("Command completed with errors:\n ${getLines(process.errorStream).joinToString("\n")}")
            null
        }
    }

    private fun getLines(inputStream: InputStream): List<String> {
        val reader = BufferedReader(inputStream.reader())
        val lines: MutableList<String> = ArrayList()
        reader.forEachLine { lines.add(it) }
        return lines
    }
}