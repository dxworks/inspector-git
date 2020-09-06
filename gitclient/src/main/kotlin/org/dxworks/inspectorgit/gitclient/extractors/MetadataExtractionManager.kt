package org.dxworks.inspectorgit.gitclient.extractors

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.GitCommitIterator
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.dxworks.inspectorgit.gitclient.extractors.impl.HunkChangeMetaExtractor
import org.dxworks.inspectorgit.gitclient.extractors.impl.LineOperationsMetaExtractor
import org.dxworks.inspectorgit.gitclient.iglog.writers.IGLogWriter
import org.dxworks.inspectorgit.gitclient.parsers.CommitParserFactory
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import java.io.File
import java.nio.file.Path

class MetadataExtractionManager(private val repoPath: Path, extractToPath: Path) {
    private val gitClient = GitClient(repoPath)

    private val commitIterator = GitCommitIterator(gitClient, 4000)

    private val extractDir = extractToPath.toFile()

    private val lineOperationsMetaExtractor = LineOperationsMetaExtractor()
    private val hunkChangeMetaExtractor = HunkChangeMetaExtractor()

    private val writtenCommitIds: MutableSet<String> = HashSet()
    private val logsOnHold: MutableList<GitLogDTO> = ArrayList()

    init {
        if (!extractDir.isDirectory && !extractDir.mkdir())
            throw IllegalArgumentException("Output directory is not a valid location: $extractToPath")
    }

    fun extract() {
        val extractFile = extractDir.resolve("${repoPath.fileName}.iglog")
        extractFile.writeText("Version\n")


        var currentCommit = if (commitIterator.hasNext()) commitIterator.next() else null

        while (currentCommit != null) {
            val commit = currentCommit
            currentCommit = null
            val numberOfParents = CommitParserFactory.getNumberOfParents(commit)
            val commits = if (numberOfParents > 1) {
                val nextCommits: MutableList<String> = ArrayList()
                for (i in 1 until numberOfParents) {
                    if (commitIterator.hasNext()) {
                        val next = commitIterator.next()
                        if (next.first() == commit.first())
                            nextCommits += next
                        else {
                            currentCommit = next
                            break
                        }
                    } else break
                }
                commit + nextCommits
            } else {
                commit
            }
            val gitLogDTO = LogParser(gitClient).parse(commits)
            swapContentWithMetadata(gitLogDTO)


            if (writtenCommitIds.containsAll(gitLogDTO.commits.flatMap { it.parentIds }.distinct())) {
                writeGitLog(extractFile, gitLogDTO)
                writeLogsOnHold(extractFile)
            } else {
                logsOnHold.add(gitLogDTO)
            }



            currentCommit = currentCommit ?: if (commitIterator.hasNext()) commitIterator.next() else null
        }
    }

    private fun writeLogsOnHold(extractFile: File, i: Int = 0) {
        if (i >= logsOnHold.size) {
            val parentCommitIds = logsOnHold[i].commits.flatMap { it.parentIds }.distinct()
            if (writtenCommitIds.containsAll(parentCommitIds)) {
                writeGitLog(extractFile, logsOnHold[i])
                logsOnHold.removeAt(i)
                writeLogsOnHold(extractFile, i)
            } else {
                writeLogsOnHold(extractFile, i + 1)
            }
        }
    }

    private fun writeGitLog(extractFile: File, gitLogDTO: GitLogDTO) {
        extractFile.appendText(toIgLog(gitLogDTO))
        writtenCommitIds.addAll(gitLogDTO.commits.map { it.id }.distinct())
    }

    private fun toIgLog(gitLogDTO: GitLogDTO) = IGLogWriter(gitLogDTO).write()

    private fun swapContentWithMetadata(gitLogDTO: GitLogDTO) {
        gitLogDTO.commits.parallelStream().forEach { commitDTO ->
            commitDTO.changes.forEach { changeDTO -> changeDTO.hunks.forEach { swapContentWithMetadata(it) } }
        }
    }

    private fun swapContentWithMetadata(hunkDTO: HunkDTO) {
        hunkDTO.lineChanges = listOf(
                ContentOnlyLineChange(lineOperationsMetaExtractor.write(hunkDTO))
//                ContentOnlyLineChange(hunkChangeMetaExtractor.write(hunkDTO))
        )
    }

    private fun getMetadata(content: String): String {
        return "${content.length} ${content.count { it.isWhitespace() }}"
    }

    private class ContentOnlyLineChange(content: String) : LineChangeDTO(LineOperation.ADD, 0, content)
}