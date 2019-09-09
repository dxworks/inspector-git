package org.dxworks.gitinspector

import lombok.extern.slf4j.Slf4j
import org.dxworks.gitinspector.parsers.LogParser
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
    private val gitBlameCommand = "$git blame -l"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    fun getLogs(): List<String> {
        LOG.info("Running log command: $gitLogCommand")
        processBuilder.command("bash", "-c", gitLogCommand)
        val process = processBuilder.start()
        val reader = BufferedReader(process.inputStream.reader())
        val lines: MutableList<String> = ArrayList()
        reader.forEachLine { lines.add(it) }
        return if (process.waitFor() == 0) {
            LOG.info("Log command finished")
            lines
        } else throw RuntimeException("Git command failed")
    }

    fun blame(revision: String, file: String): List<String> {
        LOG.info("Running blame command: $gitBlameCommand")
        processBuilder.command("bash", "-c", "$gitBlameCommand $file $revision")
        val process = processBuilder.start()
        val reader = BufferedReader(process.inputStream.reader())
        val lines: MutableList<String> = ArrayList()
        reader.forEachLine { lines.add(it) }
        return if (process.waitFor() == 0) {
            LOG.info("Blame command finished")
            lines
        } else throw RuntimeException("Git command failed")
    }
}

fun main() {

    val process = ProcessBuilder("bash", "-c", "git --version").start()
    if (process.waitFor() == 0)
        String(process.inputStream.readAllBytes()).also { println(it) }
    else
        throw IllegalStateException("This program requires a git client")

    val gitClient = GitClient(Paths.get(System.getProperty("user.home"), "Documents/dx/test"))
    val commits = LogParser(gitClient).parse(gitClient.getLogs().toMutableList())
    println(commits)
}