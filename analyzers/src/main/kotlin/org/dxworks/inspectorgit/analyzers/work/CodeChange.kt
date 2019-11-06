package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.model.AnnotatedLine

data class CodeChange(val addedLine: AnnotatedLine, val removedLine: AnnotatedLine)