package org.dxworks.inspectorgit.client.dto

import org.dxworks.inspectorgit.client.enums.LineOperation

data class LineChangeDTO(val operation: LineOperation, val number: Int, val content: String)

