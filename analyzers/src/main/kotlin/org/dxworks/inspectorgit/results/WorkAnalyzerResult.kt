package org.dxworks.inspectorgit.results

import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Commit
import org.dxworks.inspectorgit.types.CodeChange

data class WorkAnalyzerResult(val commit: Commit,
                              var newWork: MutableList<AnnotatedLine> = ArrayList(),
                              var legacyRefactor: MutableList<CodeChange> = ArrayList(),
                              var helpOthers: MutableList<CodeChange> = ArrayList(),
                              var churn: MutableList<AnnotatedLine> = ArrayList())