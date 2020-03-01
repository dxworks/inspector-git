package org.dxworks.inspectorgit.gitclient.dto

import org.dxworks.inspectorgit.gitclient.enums.LineOperation

data class LineChangeDTO(val operation: LineOperation, val number: Int, val content: String)

