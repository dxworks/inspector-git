package org.dxworks.gitsecond.data

import org.eclipse.jgit.diff.DiffEntry


data class ChangeData(var commitID: String, var oldFileName: String, var newFileName: String, var type: DiffEntry.ChangeType, var diff: String = "", var annotatedLines: MutableList<AnnotatedLineData>) {

}
