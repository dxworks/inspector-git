package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.core.model.AnnotatedLine

data class CodeChange(val addedLine: AnnotatedLine, val removedLine: AnnotatedLine)