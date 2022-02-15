package org.dxworks.inspectorgit.gitclient

import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants.Companion.gitLogMessageEnd
import org.dxworks.inspectorgit.utils.OsUtils
import org.dxworks.inspectorgit.utils.OsUtils.Companion.isUnix
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Path

class GitClient(path: Path) {
    companion object {
        private val LOG = LoggerFactory.getLogger(GitClient::class.java)
        private val git = "git"
        const val contextThreshold = "-U1"
        const val renameDetectionThreshold = "-M60%"
        const val encodingUTF8 = "--encoding=UTF8"

        //    private val renameDetectionThreshold = "--no-renames"
    }

    private val gitLogCommand =
        "log $renameDetectionThreshold -m $contextThreshold $encodingUTF8 --format=\"${IGLogConstants.commitIdPrefix}%H%n%P%n%an%n%ae%n%ad%n%cn%n%ce%n%cd %n%s%n%b%n$gitLogMessageEnd%n\" --reverse"

    private val simpleLogCommandWin =
        "log $encodingUTF8 --no-merges --find-renames --numstat --raw --format=\"commit:%H%nauthor:%an%nemail:%ae%ndate:%cD %nmessage:%n%s%n%b%nnumstat:\""
    private val simpleLogCommandUnix =
        "log $encodingUTF8 --no-merges --find-renames --numstat --raw --format=\"commit:%H%nauthor:%an%nemail:%ae%ndate:%cD%nmessage:%n%s%n%b%nnumstat:\""
    private val gitAffectedFilesCommand = "log $renameDetectionThreshold -m -1 --name-only --pretty=\"format:\""
    private val gitCommitLinksCommand =
        "log -m  $encodingUTF8 --format=\"${IGLogConstants.commitIdPrefix}%H%n%P\" --reverse"
    private val gitCountCommitsCommand = "rev-list HEAD --count"
    private val gitDiffCommand = "diff $renameDetectionThreshold $contextThreshold"
    private val gitDiffFileNamesCommand = "diff $renameDetectionThreshold --name-only"
    private val setRenameLimitCommand = "config --global diff.renameLimit"
    private val gitBlameCommand = "blame -l"
    private val gitBranchCommand = "branch"
    private val gitCloneCommand = "clone"
    private val gitCheckoutCommand = "checkout"
    private val processBuilder = ProcessBuilder()

    init {
        processBuilder.directory(path.toFile())
    }

    val branch: String? by lazy { runGitCommand(gitBranchCommand)!!.find { it.startsWith("* ") }?.removePrefix("* ") }

    fun getLogs(): List<String> = runGitCommand(gitLogCommand)!!

    fun getSimpleLog(file: File): File {
        println("Creating Git log for ${processBuilder.directory().normalize().absolutePath} in ${file.normalize().absolutePath}")
        val logCommand = if (isUnix) simpleLogCommandUnix else simpleLogCommandWin
        runGitCommand("$logCommand > \"${file.absolutePath}\"")
        println("DONE! Exported Git log for ${processBuilder.directory().normalize().absolutePath} to ${file.normalize().absolutePath}")
        return file
    }

    fun getCommitCount(): Int = runGitCommand(gitCountCommitsCommand)!!.getOrElse(0) { "0" }.toInt()

    fun setRenameLimit(limit: Int = 5000) = runGitCommand("$setRenameLimitCommand $limit")

    fun getCommitLinks(): List<String> = runGitCommand(gitCommitLinksCommand)!!

    fun getNCommitLogs(n: Int, skip: Int = 0): List<String> =
        runGitCommand("$gitLogCommand --max-count=$n --skip=$skip")!!

    fun getNCommitLogsInputStream(n: Int, skip: Int = 0): InputStream =
        getProcessForCommand("$gitLogCommand --max-count=$n --skip=$skip").inputStream

    fun diff(parent: String, revision: String, file: String): List<String> =
        runGitCommand("$gitDiffCommand $parent $revision -- $file")!!

    fun diffFileNames(parent: String, revision: String): List<String> =
        runGitCommand("$gitDiffFileNamesCommand $revision..$parent")
            ?: emptyList()

    fun blame(revision: String, file: String): List<String>? = runGitCommand("$gitBlameCommand $file $revision")

    fun affectedFiles(revision: String): List<String> = runGitCommand("$gitAffectedFilesCommand $revision")!!

    fun clone(repoUrl: String, username: String, password: String): List<String>? =
        runGitCommand("$gitCloneCommand ${buildAuthenticatedUrl(repoUrl, username, password)}")

    //TODO: write UnitTests!!!
    private fun buildAuthenticatedUrl(repoUrl: String, username: String, password: String) =
        repoUrl.replace("//[^@].*@", "//$username:${URLEncoder.encode(password, UTF_8.toString())}@")

    fun runGitCommand(args: String): List<String>? {
        val process = getProcessForCommand(args)
        val lines = getLines(process.inputStream)

        return if (process.waitFor() == 0) {
            LOG.debug("Command completed")
            lines
        } else {
            LOG.error("Command completed with errors:\n ${getLines(process.errorStream).joinToString("\n")}")
            null
        }
    }

    private fun getProcessForCommand(args: String): Process {
        val command = "$git $args"
        LOG.debug("Running command: $command")

        processBuilder.command(OsUtils.commandInterpreterName, OsUtils.interpreterArg, command)
        return processBuilder.start()
    }

    private fun getLines(inputStream: InputStream): List<String> {
        val reader = BufferedReader(inputStream.reader())
        val lines: MutableList<String> = ArrayList()
        reader.forEachLine { lines.add(it) }
        return lines
    }

    fun checkout(branch: String) = runGitCommand("$gitCheckoutCommand $branch")
}
