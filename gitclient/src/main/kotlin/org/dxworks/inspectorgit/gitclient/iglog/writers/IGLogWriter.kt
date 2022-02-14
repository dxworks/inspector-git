package org.dxworks.inspectorgit.gitclient.iglog.writers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.GitLogDTO

class IGLogWriter(private val gitLogDTO: GitLogDTO, incognito: Boolean = false) : IGWriter(incognito) {
    override fun appendLines(responseBuilder: StringBuilder) {
        gitLogDTO.commits.forEach { responseBuilder.append(IGCommitWriter(it, incognito).write()) }
    }
}
