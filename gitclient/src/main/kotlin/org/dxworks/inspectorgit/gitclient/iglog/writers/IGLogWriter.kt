package org.dxworks.inspectorgit.gitclient.iglog.writers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO

class IGLogWriter(private val gitLogDTO: GitLogDTO) : IGWriter() {
    override fun appendLines(responseBuilder: StringBuilder) {
        gitLogDTO.commits.forEach { responseBuilder.appendln(IGCommitWriter(it).write()) }
    }
}