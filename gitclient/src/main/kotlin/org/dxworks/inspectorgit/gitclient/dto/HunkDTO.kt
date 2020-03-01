package org.dxworks.inspectorgit.gitclient.dto

data class HunkDTO(val lineChanges: List<LineChangeDTO>, val type: HunkType)
