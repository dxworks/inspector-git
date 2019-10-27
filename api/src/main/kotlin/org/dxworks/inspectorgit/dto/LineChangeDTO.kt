package org.dxworks.inspectorgit.dto

import org.dxworks.inspectorgit.enums.LineOperation

data class LineChangeDTO(val operation: LineOperation, val number: Int, val content: String)

