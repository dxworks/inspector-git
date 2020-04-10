package org.dxworks.inspectorgit.gitclient.iglog.readers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.LineChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.LineOperation
import org.dxworks.inspectorgit.gitclient.extractors.impl.LineOperationsMetaExtractor
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants

class IgHunkReader {
    fun read(lines: MutableList<String>): HunkDTO {
        val meta = LineOperationsMetaExtractor().read(lines.first().removePrefix(IGLogConstants.hunkPrefixLine))
        val lineChanges = meta.addRanges.flatMap {
            getLineChangeDTOs(it, LineOperation.ADD)
        } + meta.deleteRanges.flatMap {
            getLineChangeDTOs(it, LineOperation.DELETE)
        }

        return HunkDTO(lineChanges)
    }

    private fun getLineChangeDTOs(it: Pair<Int, Int>, operation: LineOperation) =
            (it.first..it.second).map { number -> LineChangeDTO(operation, number, null) }

}
