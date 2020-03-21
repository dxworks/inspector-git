package org.dxworks.inspectorgit.gitclient.extractors

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.GitLogPager
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.dxworks.inspectorgit.gitclient.extractors.impl.HunkChangeMetaExtractor
import org.dxworks.inspectorgit.gitclient.extractors.impl.LineOperationsMetaExtractor
import org.dxworks.inspectorgit.gitclient.iglog.writers.IGLogWriter
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.utils.appFolderPath
import java.nio.file.Path
import java.nio.file.Paths

class MetadataExtractionManager(private val repoPath: Path, extractToPath: Path) {
    private val gitClient = GitClient(repoPath)

    private val gitLogPager = GitLogPager(gitClient, 1000)

    private val extractDir = extractToPath.toFile()

    private val lineOperationsMetaExtractor = LineOperationsMetaExtractor()
    private val hunkChangeMetaExtractor = HunkChangeMetaExtractor()

    init {
        if (!extractDir.isDirectory && !extractDir.mkdir())
            throw IllegalArgumentException("Output directory is not a valid location: $extractToPath")
    }

    fun extract() {
        val extractFile = extractDir.resolve("${repoPath.fileName}.iglog")
        extractFile.writeText("")


        while (gitLogPager.hasNext()) {
            val pagedCommits = gitLogPager.next()
            val commitsLines = LogParser.extractCommits(pagedCommits)
            commitsLines.forEach {
                val gitLogDTO = LogParser(gitClient).parse(it)

                swapContentWithMetadata(gitLogDTO)

                extractFile.appendText(toIgLog(gitLogDTO))
            }
        }
    }

    private fun toIgLog(gitLogDTO: GitLogDTO) = IGLogWriter(gitLogDTO).write()

    private fun swapContentWithMetadata(gitLogDTO: GitLogDTO) {
        gitLogDTO.commits.forEach { commitDTO ->
            commitDTO.changes
                    .forEach { changeDTO -> changeDTO.hunks.forEach { swapContentWithMetadata(it) } }
        }
    }

    private fun swapContentWithMetadata(hunkDTO: HunkDTO) {
        hunkDTO.lineChanges = listOf(
                ContentOnlyLineChange(lineOperationsMetaExtractor.write(hunkDTO)),
                ContentOnlyLineChange(hunkChangeMetaExtractor.write(hunkDTO))
        )
    }

    private fun getMetadata(content: String): String {
        return "${content.length} ${content.count { it.isWhitespace() }}"
    }

    private class ContentOnlyLineChange(content: String) : LineChangeDTO(LineOperation.ADD, 0, content)
}

fun main() {
    val kafkaPath = Paths.get("C:\\Users\\dnagy\\Documents\\personal\\licenta\\kafka\\kafka")
    MetadataExtractionManager(kafkaPath, appFolderPath.resolve("kafkaMeta")).extract()
}