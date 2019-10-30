package org.dxworks.inspectorgit.types

import org.dxworks.inspectorgit.model.AnnotatedLine

data class CodeChange(val addedLine: AnnotatedLine, val removedLine: AnnotatedLine)