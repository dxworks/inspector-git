package org.dxworks.inspectorgit.gitclient.jgit

import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType

class JGitChangeDTO(
    oldFileName: String,
    newFileName: String,
    type: ChangeType,
    parentCommitId: String,
    isBinary: Boolean,
    val addedLines: Number,
    val deletedLines: Number,
    val numberOfHunks: Number
) : ChangeDTO(
    oldFileName,
    newFileName,
    type,
    parentCommitId,
    isBinary,
    emptyList()
)
