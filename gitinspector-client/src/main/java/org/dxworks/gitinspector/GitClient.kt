package org.dxworks.gitinspector

import lombok.extern.slf4j.Slf4j
import org.dxworks.gitinspector.parsers.LogParser
import org.dxworks.gitinspector.utils.DTO_FOLDER_PATH
import org.dxworks.gitinspector.utils.Helper
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
    private val gitLogCommand = "$git log -M25% -c -U0 --encoding=UTF-8 --format=\"commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%ndate: %ad%nmessage:%n%s%n%b\""
    private val gitAffectedFilesCommand = "$git log -m -1 --name-only --pretty=\"format:\""
    private val gitBlameCommand = "$git blame -l"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    fun getLogs(): List<String> {
        val command = gitLogCommand
        LOG.info("Running log command: $command")
        return runCommand(command)
    }

    fun blame(revision: String, file: String): List<String> {
        val command = "$gitBlameCommand $file $revision"
        LOG.info("Running blame command: $command")
        return runCommand(command)
    }

    fun affectedFiles(revision: String): List<String> {
        val command = "$gitBlameCommand $revision"
        LOG.info("Running  command: $command")
        return runCommand(command)
    }

    private fun runCommand(command: String): List<String> {
        processBuilder.command("bash", "-c", "$command")
        val process = processBuilder.start()
        val reader = BufferedReader(process.inputStream.reader())
        val lines: MutableList<String> = ArrayList()
        reader.forEachLine { lines.add(it) }
        return if (process.waitFor() == 0) {
            LOG.info("Blame command finished")
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