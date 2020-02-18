package org.dxworks.inspectorgit.gitClient.dto

import org.dxworks.inspectorgit.gitClient.enums.LineOperation

data class LineChangeDTO(val operation: LineOperation, val number: Int)

