package org.dxworks.inspectorgit.gitclient.iglog.readers

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.dto.gitlog.HunkDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType
import org.dxworks.inspectorgit.gitclient.iglog.IGLogConstants
import org.dxworks.inspectorgit.utils.devNull

class IGChangeReader(private val igHunkReader: IgHunkReader = IgHunkReader()) {
    fun read(lines: MutableList<String>): ChangeDTO {
        val (type, isBinary) = getType(lines.removeAt(0).removePrefix(IGLogConstants.changePrefix))
        val parentCommitId = lines.removeAt(0)
        val (oldFileName, newFileName) = getFileName(lines, type)

        var currentHunkLines: MutableList<String> = ArrayList()
        val hunks: MutableList<HunkDTO> = ArrayList();
        if (!isBinary) {
            lines.forEach {
                if (it.startsWith(IGLogConstants.hunkPrefixLine)) {
                    if (currentHunkLines.isNotEmpty()) hunks.add(igHunkReader.read(currentHunkLines))
                    currentHunkLines = ArrayList()
                }
                currentHunkLines.add(it)
            }
            if (currentHunkLines.isNotEmpty()) hunks.add(igHunkReader.read(currentHunkLines))
        }

        return ChangeDTO(oldFileName,
                newFileName,
                type,
                parentCommitId,
                isBinary,
                hunks)
    }

    private fun getFileName(lines: MutableList<String>, type: ChangeType): Pair<String, String> {
        val fileName = lines.removeAt(0)
        return when (type) {
            ChangeType.ADD -> Pair(devNull, fileName)
            ChangeType.DELETE -> Pair(fileName, devNull)
            ChangeType.RENAME -> Pair(fileName, lines.removeAt(0))
            ChangeType.MODIFY -> Pair(fileName, fileName)
        }
    }

    private fun getType(line: String): Pair<ChangeType, Boolean> {
        val type = when (line[0]) {
            'A' -> ChangeType.ADD
            'D' -> ChangeType.DELETE
            'R' -> ChangeType.RENAME
            else -> ChangeType.MODIFY
        }
        return Pair(type, line.length > 1)
    }
}
