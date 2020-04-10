package org.dxworks.inspectorgit.gitclient.iglog.readers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.CommitDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import java.io.InputStream

class IGLogReader(private val commitReader: IGCommitReader = IGCommitReader()) {
    fun read(stream: InputStream): GitLogDTO {
        val reader = stream.bufferedReader()
        val iglogVersion = reader.readLine()
        var currentCommitLines: MutableList<String> = ArrayList()
        val commits: MutableList<CommitDTO> = ArrayList();
        reader.forEachLine {
            if (it.startsWith(IGLogConstants.commitIdPrefix)) {
                if (currentCommitLines.isNotEmpty()) commits.add(commitReader.read(currentCommitLines))
                currentCommitLines = ArrayList()
            }
            currentCommitLines.add(it)
        }
        if (currentCommitLines.isNotEmpty()) commits.add(commitReader.read(currentCommitLines))
        return GitLogDTO(commits)
    }
}