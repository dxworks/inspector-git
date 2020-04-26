package org.dxworks.inspectorgit.core.model

import org.dxworks.inspectorgit.gitclient.enums.LineOperation

data class LineChange(val operation: LineOperation, val annotatedLine: AnnotatedLine, val commit: Commit)

