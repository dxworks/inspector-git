package org.dxworks.inspectorgit.gitclient.iglog.writers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants

class IGHunkWriter(private val hunkDTO: HunkDTO) : IGWriter() {

    override fun appendLines(responseBuilder: StringBuilder) {
        responseBuilder.append(IGLogConstants.hunkPrefixLine)
        hunkDTO.lineChanges.forEach { responseBuilder.appendLine(it.content) }
    }
}
