package org.dxworks.inspectorgit.gitclient.dto.iglog

import org.dxworks.inspectorgit.gitclient.dto.ChangeInfoDTO
import org.dxworks.inspectorgit.gitclient.enums.ChangeType

class IgChangeDTO(
        oldFileName: String,
        newFileName: String,
        type: ChangeType,
        parentCommitId: String,
        isBinary: Boolean,
        val igHunkDTO: IgHunkDTO
) : ChangeInfoDTO(
        oldFileName,
        newFileName,
        type,
        parentCommitId,
        isBinary
)
