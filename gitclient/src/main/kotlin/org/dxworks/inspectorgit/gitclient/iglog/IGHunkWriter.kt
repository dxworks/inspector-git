package org.dxworks.inspectorgit.gitclient.iglog

import org.dxworks.inspectorgit.gitclient.dto.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.LineOperation

class IGHunkWriter(private val hunkDTO: HunkDTO) : IGWriter() {

    override fun appendLines(responseBuilder: StringBuilder) {
        responseBuilder.appendln(IGLogConstants.hunkPrefixLine)
        hunkDTO.lineChanges.forEach { responseBuilder.appendln(getFormattedLine(it)) }
    }

    private fun getFormattedLine(lineChangeDTO: LineChangeDTO) =
            "${getOperation(lineChangeDTO)}${lineChangeDTO.number})${lineChangeDTO.content}"

    private fun getOperation(lineChangeDTO: LineChangeDTO) = when (lineChangeDTO.operation) {
        LineOperation.ADD -> "+"
        LineOperation.DELETE -> "-"
    }

}
