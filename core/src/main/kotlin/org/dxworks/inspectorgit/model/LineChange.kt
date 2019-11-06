package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.client.enums.LineOperation

data class LineChange(var operation: LineOperation, var annotatedLine: AnnotatedLine)

