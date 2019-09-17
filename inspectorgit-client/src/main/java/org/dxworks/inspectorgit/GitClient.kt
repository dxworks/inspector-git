package org.dxworks.inspectorgit

import lombok.extern.slf4j.Slf4j
import org.dxworks.inspectorgit.parsers.LogParser
import org.dxworks.inspectorgit.utils.DTO_FOLDER_PATH
import org.dxworks.inspectorgit.utils.Helper
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
class GitClient(path: Path) {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitClient::class.java)
    }

    private val git = "git"
    private val renameDetectionThreshold = "-M25%"
    private val contextThreshold = "-U0"


    private val gitLogCommand = "$git log $renameDetectionThreshold -c $contextThreshold --encoding=UTF-8 --format=\"commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%nauthor date: %ad%ncommitter name: %cn%ncommitter email: %ce%ncommitter date: %cd%nmessage:%n%s%n%b\""
    private val gitAffectedFilesCommand = "$git log $renameDetectionThreshold -m -1 --name-only --pretty=\"format:\""
    private val gitDiffCommand = "$git diff $renameDetectionThreshold $contextThreshold"
    private val gitBlameCommand = "$git blame -l"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    fun getLogs(): List<String> = runCommand(gitLogCommand)

    fun diff(parent: String, revision: String, file: String): List<String> = runCommand("$gitDiffCommand $parent $revision -- $file")

    fun blame(revision: String, file: String): List<String> {
        return runCommand("$gitBlameCommand $file $revision")
    }

    fun affectedFiles(revision: String): List<String> = runCommand("$gitAffectedFilesCommand $revision")

    private fun runCommand(command: String): List<String> {
        LOG.info("Running command: $command")
        processBuilder.command("bash", "-c", command)
        val process = processBuilder.start()
        val reader = BufferedReader(process.inputStream.reader())
        val lines: MutableList<String> = ArrayList()
        reader.forEachLine { lines.add(it) }
        return if (process.waitFor() == 0) {
            LOG.info("Command completed")
            lines
        } else {
            LOG.error("Command completed with errors")
            emptyList()
        }
    }
}

fun main() {
    val gitClient = GitClient(Paths.get("/home/darius/.dx-platform/projects/kafka/repository/kafka"))
    val projectDTO = LogParser(gitClient).parse(gitClient.getLogs().toMutableList())
    Helper.toJsonFile(DTO_FOLDER_PATH.resolve("kafka.json"), projectDTO)
}