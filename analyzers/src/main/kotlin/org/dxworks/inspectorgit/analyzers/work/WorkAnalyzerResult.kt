package org.dxworks.inspectorgit.analyzers.work

import org.dxworks.inspectorgit.model.AnnotatedLine
import org.dxworks.inspectorgit.model.Commit

data class WorkAnalyzerResult(val commit: Commit,
                              var newWork: MutableList<AnnotatedLine> = ArrayList(),
                              var legacyRefactor: MutableList<CodeChange> = ArrayList(),
                              var helpOthers: MutableList<CodeChange> = ArrayList(),
                              var churn: MutableList<AnnotatedLine> = ArrayList())