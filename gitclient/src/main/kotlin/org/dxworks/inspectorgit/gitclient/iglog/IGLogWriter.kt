package org.dxworks.inspectorgit.gitclient.iglog

import org.dxworks.inspectorgit.gitclient.dto.GitLogDTO

class IGLogWriter(private val gitLogDTO: GitLogDTO) : IGWriter() {
    override fun appendLines(responseBuilder: StringBuilder) {
        gitLogDTO.commits.forEach { responseBuilder.appendln(IGCommitWriter(it).write()) }
    }
}