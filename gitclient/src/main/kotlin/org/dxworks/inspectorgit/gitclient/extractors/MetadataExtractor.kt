package org.dxworks.inspectorgit.gitclient.extractors

import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.GitLogPager
import org.dxworks.inspectorgit.gitclient.dto.GitLogDTO
import org.dxworks.inspectorgit.gitclient.dto.HunkDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogWriter
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.utils.appFolderPath
import java.nio.file.Path
import java.nio.file.Paths

class MetadataExtractor(private val repoPath: Path, extractToPath: Path) {
    private val gitClient = GitClient(repoPath)

    private val gitLogPager = GitLogPager(gitClient)

    private val extractDir = extractToPath.toFile()

    init {
        if (!extractDir.isDirectory && !extractDir.mkdir())
            throw IllegalArgumentException("Output directory is not a valid location: $extractToPath")
    }

    fun extract() {
        val extractFile = extractDir.resolve("${repoPath.fileName}.iglog")
        extractFile.createNewFile()
        while (gitLogPager.hasNext()) {
            val pagedCommits = gitLogPager.next()
            val gitLogDTO = LogParser(gitClient).parse(pagedCommits)

            swapContentWithMetadata(gitLogDTO)

            extractFile.writeText(toIgLog(gitLogDTO))
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
        hunkDTO.lineChanges.forEach { it.content = getMetadata(it.content) }
    }

    private fun getMetadata(content: String): String {
        return "${content.length} ${content.count { it.isWhitespace() }}"
    }
}

fun main() {
    val kafkaPath = Paths.get("C:\\Users\\dnagy\\Documents\\personal\\licenta\\kafka\\kafka")
    MetadataExtractor(kafkaPath, appFolderPath.resolve("kafkaMetadata")).extract()
}