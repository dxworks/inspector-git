package org.dxworks.inspectorgit.gitclient.dto.gitlog

import org.dxworks.inspectorgit.gitclient.enums.LineOperation

open class LineChangeDTO(val operation: LineOperation, val number: Int, var content: String?)
