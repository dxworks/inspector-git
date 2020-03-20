package org.dxworks.inspectorgit.gitclient.dto.iglog

open class LineOperationsMeta(
        val addRanges: List<Pair<Int, Int>>,
        val deleteRanges: List<Pair<Int, Int>>
)