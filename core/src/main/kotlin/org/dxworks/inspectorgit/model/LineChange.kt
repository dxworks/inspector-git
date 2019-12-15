package org.dxworks.inspectorgit.model

import org.dxworks.inspectorgit.gitClient.enums.LineOperation

data class LineChange(var operation: LineOperation, var annotatedLine: AnnotatedLine)

