package org.dxworks.gitinspector

import org.dxworks.gitinspector.parsers.LogParser
import java.nio.file.Path
import java.nio.file.Paths

class GitClient(path: Path) {
    private val git = "git"
    private val gitLogCommand = "$git log -M5% -c -U0 --format=\"commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%ndate: %at%nmessage:%n%s%n%b\""
    private val gitBlameCommand = "$git blame -l"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    fun getLogs(): List<String> {
        processBuilder.command("bash", "-c", gitLogCommand)
        val process = processBuilder.start()
        return if (process.waitFor() == 0) splitOutput(process) else throw RuntimeException("Git command failed")
    }

    fun blame(revision: String, file: String): List<String> {
        processBuilder.command("bash", "-c", "$gitBlameCommand $file $revision")
        val process = processBuilder.start()
        return if (process.waitFor() == 0) splitOutput(process) else throw RuntimeException("Git command failed")
    }

    private fun splitOutput(process: Process) = String(process.inputStream.readAllBytes()).split("\n")
}

fun main() {

    val process = ProcessBuilder("bash", "-c", "git --version").start()
    if (process.waitFor() == 0)
        String(process.inputStream.readAllBytes()).also { println(it) }
    else
        throw IllegalStateException("This program requires a git client")

    val gitClient = GitClient(Paths.get(System.getProperty("user.home"), "Documents/dx/test"))
    val commits = LogParser(gitClient).parse(gitClient.getLogs())
    println(commits)
}