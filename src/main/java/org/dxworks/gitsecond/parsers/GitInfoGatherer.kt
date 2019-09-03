package org.dxworks.gitsecond.parsers

import java.nio.file.Path

class GitInfoGatherer(private val path: Path) {
    private val git = "git"
    private val gitLogCommand = "$git log -p -M5% -c -U0 --format=\"commit: %H%nparents: %P%nauthor name: %an%nauthor email: %ae%ndate: %ad%nmessage:%n%s%n%b\""
    private val gitShowChangedFilesCommand = "$git show --name-only --oneline"
    private val gitBlameCommand = "$git blame -p"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    fun getLogs(): List<String> {
        processBuilder.command("bash", "-c", gitLogCommand)
        val process = processBuilder.start()
        val exitVal = process.waitFor()
        return if (exitVal == 0) splitOutput(process) else throw RuntimeException("Git command failed")
    }

    fun getChangedFilesForRevision(revision: String): List<String> {

        processBuilder.command("bash", "-c", "$gitShowChangedFilesCommand $revision")
        val process = processBuilder.start()
        val exitVal = process.waitFor()
        return if (exitVal == 0) prepareCommandOutput(process) else throw RuntimeException("Git command failed")
    }

    private fun prepareCommandOutput(process: Process): List<String> {
        val output = String(process.inputStream.readAllBytes()).split("\n").toMutableList()
        output.removeAt(0)
        return output.filter { it.isNotBlank() }
    }

    fun blame(revision: String, file: String): List<String> {
        processBuilder.command("bash", "-c", "$gitBlameCommand $file $revision")
        val process = processBuilder.start()
        val exitVal = process.waitFor()
        return if (exitVal == 0) splitOutput(process) else throw RuntimeException("Git command failed")
    }

    private fun splitOutput(process: Process) = String(process.inputStream.readAllBytes()).split("\n")

}