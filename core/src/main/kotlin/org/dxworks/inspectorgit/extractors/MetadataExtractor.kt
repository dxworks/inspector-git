package org.dxworks.inspectorgit.extractors

import org.dxworks.inspectorgit.extractors.dto.ChangeMetadataDTO
import org.dxworks.inspectorgit.extractors.dto.CommitMetadataDTO
import org.dxworks.inspectorgit.gitclient.GitClient
import org.dxworks.inspectorgit.gitclient.GitLogPager
import org.dxworks.inspectorgit.gitclient.dto.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.CommitDTO
import org.dxworks.inspectorgit.gitclient.parsers.LogParser
import org.dxworks.inspectorgit.utils.appFolderPath
import java.nio.file.Path
import java.nio.file.Paths

class MetadataExtractor(repoPath: Path, private val extractToPath: Path) {
    private val gitClient = GitClient(repoPath)

    private val gitLogPager = GitLogPager(gitClient)

    private val extractDir = extractToPath.toFile()

    init {
        if (!extractDir.isDirectory && !extractDir.mkdir())
            throw IllegalArgumentException("Output directory is not a valid location: $extractToPath")
    }

    fun extract() {
        while (gitLogPager.hasNext()) {
            val pagedCommits = gitLogPager.next()
            val gitLogDTO = LogParser(gitClient).parse(pagedCommits)

            val commitMetadataList = gitLogDTO.commits.map { extractCommitMetadata(it) }
            saveMetadata(commitMetadataList)
        }
    }

    private fun saveMetadata(commitMetadataList: List<CommitMetadataDTO>) {

    }

    private fun extractCommitMetadata(commitDTO: CommitDTO) =
            CommitMetadataDTO(commitDTO, commitDTO.changes.map { extractChangeMetadata(it) })


    private fun extractChangeMetadata(changeDTO: ChangeDTO): ChangeMetadataDTO {
        return ChangeMetadataDTO()
    }

}

fun main() {
    val kafkaPath = Paths.get("C:\\Users\\nagyd\\Documents\\DX\\kafkaRepo\\kafka")
    MetadataExtractor(kafkaPath, appFolderPath.resolve("kafkaMetadata")).extract()
}