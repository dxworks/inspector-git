package org.dxworks.inspectorgit.gitclient.dto.gitlog.simple

import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.gitclient.iglog.readers.IGLogReader
import java.io.File
import java.nio.file.Paths

class SimpleLogWriter {
    fun write(iglogFile: File) {
        val logDTO = IGLogReader().read(iglogFile.inputStream())
        iglogFile.parentFile.resolve("${iglogFile.nameWithoutExtension}.git").writeText(toSimpleCommitLog(logDTO))
    }

    fun write(gitLogDTO: GitLogDTO, name: String) =
        Paths.get(".").resolve("$name.git").toFile().writeText(toSimpleCommitLog(gitLogDTO))

    private fun toSimpleCommitLog(gitLogDTO: GitLogDTO) =
        gitLogDTO.commits.reversed().joinToString(separator = "\n") {
            SimpleCommitDTO(it).toString()
        }
}
