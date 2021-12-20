package org.dxworks.inspectorgit.gitclient.jgit

import com.fasterxml.jackson.annotation.JsonProperty
import org.dxworks.inspectorgit.gitclient.dto.gitlog.ChangeDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType

class JGitChangeDTO(
    oldFileName: String,
    newFileName: String,
    type: ChangeType,
    parentCommitId: String,
    isBinary: Boolean,
    @JsonProperty("a")
    val addedLines: Number,
    @JsonProperty("d")
    val deletedLines: Number,
    @JsonProperty("n")
    val numberOfHunks: Number
) : ChangeDTO(
    oldFileName,
    newFileName,
    type,
    parentCommitId,
    isBinary,
    emptyList()
)
